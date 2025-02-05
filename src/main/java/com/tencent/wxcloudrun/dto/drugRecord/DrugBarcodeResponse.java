package com.tencent.wxcloudrun.dto.drugRecord;

import lombok.Data;

@Data
public class DrugBarcodeResponse {
    private String msg;
    private boolean success;
    private int code;
    private DrugBarcodeData data;

    @Data
    public static class DrugBarcodeData {
        private String orderNo;
        private DrugInfo info;
    }

    @Data
    public static class DrugInfo {
        private String dosage;
        private String manuName;
        private String note;
        private String img;
        private String other;
        private String code;
        private String purpose;
        private String taboo;
        private String approval;
        private String storage;
        private String basis;
        private String manuAddress;
        private String spec;
        private String character;
        private String name;
        private String trademark;
        private String consideration;
        private String validity;
        private int ret_code;
    }
} 