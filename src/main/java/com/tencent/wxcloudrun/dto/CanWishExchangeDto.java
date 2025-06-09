package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class CanWishExchangeDto {

    public CanWishExchangeDto(boolean is, String msg) {
        this.is = is;
        this.msg = msg;
    }
    private boolean is;
    private String msg;
}
