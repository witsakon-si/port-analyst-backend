package com.mport.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.mport.domain.dto.ApplicationDTO;
import com.mport.domain.dto.FxPriceDTO;
import com.mport.domain.dto.TargetPriceDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PriceSocketClient extends TextWebSocketHandler {

    @Getter
    private WebSocketSession clientSession;

    private ApplicationDTO applicationDTO;
    private HazelcastInstance hz;
    private String API_KEY;
    private final ObjectMapper mapper;

    public PriceSocketClient(ApplicationDTO applicationDTO, ObjectMapper mapper, HazelcastInstance hz, String API_KEY) throws ExecutionException, InterruptedException {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        this.clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), URI.create("wss://ws.finnhub.io?token=" + API_KEY)).get();
        this.applicationDTO = applicationDTO;
        this.mapper = mapper;
        this.hz = hz;
        this.API_KEY = API_KEY;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String json = message.getPayload();
            if (json.contains("data")) {
                HashMap result = new ObjectMapper().readValue(json, HashMap.class);

                IMap<String, FxPriceDTO> priceDTOMap = hz.getMap("REALTIME_PRICE");

                List<HashMap> data = (List<HashMap>) result.get("data");
                if (data.size() > 0) {
                    String symbol = data.get(0).get("s").toString();
                    if (symbol.contains("OANDA:")) {
                        symbol = symbol.replace("OANDA:", "");
                        symbol = symbol.replace("_", "-");
                    }
                    BigDecimal current = new BigDecimal(data.get(0).get("p").toString());
                    current = current.setScale(4, RoundingMode.HALF_UP);
                    Date dt = new Date(Long.parseLong(data.get(0).get("t").toString()));

                    FxPriceDTO fxPriceDTO = new FxPriceDTO();
                    fxPriceDTO.setLastUpdate(dt);
                    fxPriceDTO.setSymbol(symbol);
                    fxPriceDTO.setCurrent(current);

                    FxPriceDTO prevFxPriceDTO = priceDTOMap.get(symbol);
                    if (prevFxPriceDTO != null) {
                        fxPriceDTO.setLastClose(prevFxPriceDTO.getLastClose());
                        fxPriceDTO.setOpen(prevFxPriceDTO.getOpen());
                        fxPriceDTO.setHigh(prevFxPriceDTO.getHigh());
                        fxPriceDTO.setLow(prevFxPriceDTO.getLow());
                        fxPriceDTO.setChange(prevFxPriceDTO.getChange());
                        fxPriceDTO.setPercentChange(prevFxPriceDTO.getPercentChange());
                    }
                    if (applicationDTO.getAssetRefTh().containsKey(fxPriceDTO.getSymbol())) {
                        fxPriceDTO.setUnderlying(true);
                        String symbolTh = applicationDTO.getAssetRefTh().get(fxPriceDTO.getSymbol()).getSymbolTh();
                        if (priceDTOMap.containsKey(symbolTh)) {
                            List<TargetPriceDTO> targetPrices = new ArrayList<>(priceDTOMap.get(symbolTh).getTargetPrices());
                            TreeMap<BigDecimal, TargetPriceDTO> sortMap = new TreeMap<>();
                            for (TargetPriceDTO targetDTO : targetPrices) {
                                targetDTO.setClosest(false);
                                sortMap.put(targetDTO.getRefPrice().subtract(fxPriceDTO.getCurrent()).abs(), targetDTO);
                            }
                            sortMap.firstEntry().getValue().setClosest(true);

                            FxPriceDTO priceDTO = SerializationUtils.clone(priceDTOMap.get(symbolTh));
                            priceDTO.setTargetPrices(targetPrices);
                            priceDTO.setLastUpdate(dt);
                            priceDTOMap.put(symbolTh, priceDTO);
                        }
                    }
                    priceDTOMap.put(symbol, fxPriceDTO);

                    // calculate HKD-THB
                    if (symbol.equals("USD-HKD") && priceDTOMap.containsKey("USD-THB")) {
                        FxPriceDTO fxPrice = new FxPriceDTO();
                        fxPrice.setSymbol("HKD-THB");
                        fxPrice.setCurrent(priceDTOMap.get("USD-THB").getCurrent().divide(current, 4, RoundingMode.HALF_UP));
                        priceDTOMap.put("HKD-THB", fxPrice);
                    }
                }
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"TSLA\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"MSFT\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"NVDA\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"GOOG\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"AMZN\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"META\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"OANDA:USD_THB\"}"));
            session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"OANDA:XAU_USD\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("afterConnectionClosedV2: {}", status.getReason());
    }

}
