package com.tencent.wxcloudrun.service.impl.drugRecord;

import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.AppSigner;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.dto.drugRecord.DrugBarcodeResponse;
import com.tencent.wxcloudrun.dto.drugRecord.DrugCodeResponse;
import com.tencent.wxcloudrun.model.drugRecord.Drugs;
import com.tencent.wxcloudrun.service.drugRecord.DrugBarcodeService;
import com.tencent.wxcloudrun.service.drugRecord.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugBarcodeServiceImpl implements DrugBarcodeService {
    
    private static final String ACCESS_KEY = "f5ec5828be9c44e4b04cda80d28d10e0";
    private static final String SECRET_KEY = "82ddf573d8664c5d8b5bc6e1a56455d0";
    private static final String API_URL = "http://gwgp-w8ah4tjhazs.n.bdcloudapi.com/v4/drug_shape_code/query";
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private DrugsService drugsService;
    
    @Override
    public DrugCodeResponse queryByCode(String code) {
        // 1. 先查询数据库
        Drugs existingDrugs = drugsService.findByCode(code);
        if (existingDrugs != null) {
            return convertToResponse(existingDrugs);
        }
        
        // 2. 数据库没有，调用API查询
        try {
            ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.POST, API_URL);
            request.setCredentials(ACCESS_KEY, SECRET_KEY);
            request.addHeaderParameter("Content-Type", "application/json;charset=UTF-8");
            request.addQueryParameter("code", code);
            
            ApiExplorerClient client = new ApiExplorerClient(new AppSigner());
            ApiExplorerResponse response = client.sendRequest(request);
            
            DrugBarcodeResponse barcodeResponse = objectMapper.readValue(response.getResult(), DrugBarcodeResponse.class);
            
            if (!barcodeResponse.isSuccess() || barcodeResponse.getData() == null || barcodeResponse.getData().getInfo() == null) {
                return null;
            }
            
            // 3. 直接返回API结果，不保存到数据库
            DrugBarcodeResponse.DrugInfo info = barcodeResponse.getData().getInfo();
            DrugCodeResponse drugCodeResponse = new DrugCodeResponse();
            drugCodeResponse.setCode(info.getCode());
            drugCodeResponse.setName(info.getName());
            drugCodeResponse.setManuName(info.getManuName());
            drugCodeResponse.setImg(info.getImg());
            drugCodeResponse.setSpec(info.getSpec());
            
            return drugCodeResponse;
        } catch (Exception e) {
            throw new RuntimeException("调用条形码查询接口失败", e);
        }
    }
    
    private DrugCodeResponse convertToResponse(Drugs drugs) {
        DrugCodeResponse response = new DrugCodeResponse();
        response.setCode(drugs.getCode());
        response.setName(drugs.getName());
        response.setManuName(drugs.getManuName());
        response.setImg(drugs.getImg());
        response.setSpec(drugs.getSpec());
        return response;
    }
} 