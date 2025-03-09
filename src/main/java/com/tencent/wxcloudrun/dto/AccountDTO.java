package com.tencent.wxcloudrun.dto;

import com.tencent.wxcloudrun.model.Account;
import com.tencent.wxcloudrun.model.InterestRate;
import lombok.Data;
import java.io.Serializable;

@Data
public class AccountDTO implements Serializable {
    private Account account;
    private InterestRate currentRate;

    public AccountDTO(Account account, InterestRate currentRate) {
        this.account = account;
        this.currentRate = currentRate;
    }
} 