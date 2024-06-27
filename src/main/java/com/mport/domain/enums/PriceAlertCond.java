package com.mport.domain.enums;

import com.mport.domain.dto.SimpleDTO;

import java.util.ArrayList;
import java.util.List;

public enum PriceAlertCond {
    GE(">="), LE("<=");

    private String msg;
    private static final List<SimpleDTO> option;

    PriceAlertCond(String msg) {
        this.msg = msg;
    }

    static {
        option = new ArrayList<>();
        for (PriceAlertCond v : PriceAlertCond.values()) {
            option.add(new SimpleDTO(v.name(), v.getMsg()));
        }
    }

    public String getMsg() {
        return msg;
    }

    public static List<SimpleDTO> getOption() {
        return option;
    }
}

