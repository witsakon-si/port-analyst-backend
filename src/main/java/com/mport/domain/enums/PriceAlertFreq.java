package com.mport.domain.enums;

import com.mport.domain.dto.SimpleDTO;

import java.util.ArrayList;
import java.util.List;

public enum PriceAlertFreq {
    ONETIME("One time", 0),
    SEC_10("Every 10 sec", 10),
    SEC_30("Every 30 sec", 30),
    MIN_1("Every 1 min", 60),
    MIN_5("Every 5 min", 300),
    MIN_10("Every 10 min", 600),
    MIN_30("Every 30 min", 1800),
    HOUR_1("Every 1 hour", 3600);

    private String msg;
    private int second;
    private static final List<SimpleDTO> option;

    PriceAlertFreq(String msg, int second) {
        this.msg = msg;
        this.second = second;
    }

    static {
        option = new ArrayList<>();
        for (PriceAlertFreq v : PriceAlertFreq.values()) {
            option.add(new SimpleDTO(v.name(), v.getMsg()));
        }
    }

    public String getMsg() {
        return msg;
    }

    public int getSecond() {
        return second;
    }

    public static List<SimpleDTO> getOption() {
        return option;
    }
}
