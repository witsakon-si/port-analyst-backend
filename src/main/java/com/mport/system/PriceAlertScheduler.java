package com.mport.system;

import com.mport.domain.enums.PriceAlertCond;
import com.mport.domain.enums.PriceAlertFreq;
import com.mport.domain.model.PriceAlert;
import com.mport.domain.repository.PriceAlertRepository;
import com.mport.domain.service.LineNotifyService;
import com.mport.domain.service.PriceService;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class PriceAlertScheduler {

    private final PriceService priceService;
    private final PriceAlertRepository priceAlertRepository;
    private final LineNotifyService lineNotifyService;

    @Autowired
    public PriceAlertScheduler(PriceService priceService, PriceAlertRepository priceAlertRepository, LineNotifyService lineNotifyService) {
        this.priceService = priceService;
        this.priceAlertRepository = priceAlertRepository;
        this.lineNotifyService = lineNotifyService;
    }

    @Scheduled(fixedDelay = 5000)
    public void cronScheduleTask() {
        Date now = DateTimeUtil.now();
        try {
            List<PriceAlert> priceAlertList = priceAlertRepository.findByActiveOrderBySymbolAscConditionDescPriceAsc(true);
            if (priceAlertList.isEmpty()) {
                return;
            }
            for (PriceAlert priceAlert : priceAlertList) {
                BigDecimal price = priceService.getForexPrice(priceAlert.getSymbol());

                if (price.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                String priceStr = String.format("%.2f", price.doubleValue());
                String targetPriceStr = String.format("%.2f", priceAlert.getPrice().doubleValue());
                String msg = null;
                if (priceAlert.getCondition() == PriceAlertCond.GE) {
                    if (price.compareTo(priceAlert.getPrice()) >= 0) {
                        msg = priceAlert.getSymbol() + "@" + priceStr + " >= " + targetPriceStr;
                    }
                } else if (priceAlert.getCondition() == PriceAlertCond.LE) {
                    if (price.compareTo(priceAlert.getPrice()) <= 0) {
                        msg = priceAlert.getSymbol() + "@" + priceStr + " <= " + targetPriceStr;
                    }
                }
                if (msg != null) {
                    if (priceAlert.getFrequency() == PriceAlertFreq.ONETIME) {
                        addNoticeToQueue(msg);
                        priceAlert.setActive(false);
                        priceAlert.setLastNotice(now);
                    } else {
                        Date nextAlert = priceAlert.getLastNotice() == null ? DateTimeUtil.datePlusSecond(now, -1) : DateTimeUtil.datePlusSecond(priceAlert.getLastNotice(), priceAlert.getFrequency().getSecond());
                        if (now.after(nextAlert)) {
                            addNoticeToQueue(msg);
                            priceAlert.setLastNotice(now);
                        }
                    }
                }
            }
            priceAlertRepository.saveAll(priceAlertList);
        } catch (Exception e) {
            log.error("Error PriceAlertScheduler: {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("PriceAlertScheduler - finish (used time: {} ms)", DateTimeUtil.now().getTime() - now.getTime());
    }

    public void addNoticeToQueue(String msg) throws Exception {
        lineNotifyService.sendLineNotifyMessages(msg);
    }
}
