package com.tencent.wxcloudrun.service.drugRecord;

import com.tencent.wxcloudrun.dto.drugRecord.DrugCodeResponse;

public interface DrugBarcodeService {
    DrugCodeResponse queryByCode(String code);
} 