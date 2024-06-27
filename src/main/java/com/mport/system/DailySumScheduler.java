package com.mport.system;

import com.mport.domain.dto.AccountInfoDTO;
import com.mport.domain.dto.HistoryDTO;
import com.mport.domain.dto.TransactionDTO;
import com.mport.domain.model.DailyAsset;
import com.mport.domain.model.DailySum;
import com.mport.domain.repository.DailyAssetRepository;
import com.mport.domain.repository.DailySumRepository;
import com.mport.domain.service.CashInOutService;
import com.mport.domain.service.HistoryService;
import com.mport.domain.service.LineNotifyService;
import com.mport.domain.service.PriceService;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DailySumScheduler {

    private final PriceService priceService;
    private final HistoryService historyService;
    private final CashInOutService cashInOutService;
    private final DailySumRepository dailySumRepository;
    private final DailyAssetRepository dailyAssetRepository;
    private final LineNotifyService lineNotifyService;

    @Autowired
    public DailySumScheduler(PriceService priceService, HistoryService historyService, CashInOutService cashInOutService, DailySumRepository dailySumRepository, DailyAssetRepository dailyAssetRepository, LineNotifyService lineNotifyService) {
        this.priceService = priceService;
        this.historyService = historyService;
        this.cashInOutService = cashInOutService;
        this.dailySumRepository = dailySumRepository;
        this.dailyAssetRepository = dailyAssetRepository;
        this.lineNotifyService = lineNotifyService;
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void cronScheduleTask() {
        Date now = DateTimeUtil.now();
        log.info("DailySumScheduler - start {}", now);
        try {
            Date date = DateTimeUtil.getOnlyDate(now);
            priceService.clearTable();
            List<String> errorMsg = priceService.syncPrice();
            String remark = errorMsg.isEmpty() ? null : String.join(", ", errorMsg);

            Map<String, AccountInfoDTO> accountInfoMap = cashInOutService.getAllAccountInfo();
            List<String> types = historyService.getAllType();
            List<TransactionDTO> list = historyService.getAllHistory(accountInfoMap, types);

            List<DailySum> dailySumList = new ArrayList<>();
            List<DailyAsset> dailyAssetList = new ArrayList<>();
            for (TransactionDTO transactionDTO : list ) {
                DailySum dailySum = new DailySum();
                AccountInfoDTO acctInfo = transactionDTO.getAccountInfo();
                dailySum.setType(transactionDTO.getType());
                dailySum.setCost(acctInfo.getCost());
                dailySum.setNetBalance(acctInfo.getNetBalance());
                dailySum.setProfitLoss(acctInfo.getProfitLoss());
                dailySum.setPercentPL(acctInfo.getPercentPL());
                dailySum.setDate(date);
                dailySum.setCreatedAt(now);
                dailySum.setRemark(remark);
                if (dailySum.getNetBalance().compareTo(BigDecimal.ZERO) != 0) {
                    dailySumList.add(dailySum);
                }

                List<HistoryDTO> historyDTOS = transactionDTO.getList();
                List<HistoryDTO> sumActiveHistory = historyDTOS.stream().filter(h -> h.getId() == null && h.getStatus().equals("active"))
                        .collect(Collectors.toList());
                for (HistoryDTO historyDTO : sumActiveHistory) {
                    DailyAsset dailyAsset = new DailyAsset();
                    dailyAsset.setType(historyDTO.getType());
                    dailyAsset.setName(historyDTO.getName());
                    dailyAsset.setCost(historyDTO.getNetAmount());
                    dailyAsset.setNetBalance(historyDTO.getAmount());
                    dailyAsset.setProfitLoss(historyDTO.getUnPL());
                    dailyAsset.setDate(date);
                    dailyAsset.setCreatedAt(now);
                    dailyAsset.setRemark(remark);
                    if (dailyAsset.getNetBalance().compareTo(BigDecimal.ZERO) != 0) {
                        dailyAssetList.add(dailyAsset);
                    }
                }
            }

            dailySumRepository.saveAll(dailySumList);
            dailyAssetRepository.saveAll(dailyAssetList);
            lineNotifyService.sendLineNotifyMessages("DailySumScheduler - finish");

        } catch (Exception e) {
            log.error("Error DailySumScheduler: {}", e.getMessage());
            e.printStackTrace();
            try {
                lineNotifyService.sendLineNotifyMessages("Error DailySumScheduler" + e.getMessage());
            } catch (Exception ex) {
                log.error("Error sendLineNotifyMessages: {}", ex.getMessage());
                e.printStackTrace();
            }
        }
        log.info("DailySumScheduler - finish {}", DateTimeUtil.now());
    }
}
