package com.tencent.wxcloudrun.service.drugRecord;

import com.tencent.wxcloudrun.dto.PageResponse;
import com.tencent.wxcloudrun.model.drugRecord.Drugs;
import java.util.List;

public interface DrugsService {
    Drugs getByIdAndOpenid(Integer id, String openid);
    
    PageResponse<Drugs> listByCondition(String openid, String name, String code, Integer pageNum, Integer pageSize);
    
    int insert(Drugs drugs);
    
    int update(Drugs drugs);
    
    int deleteByIdAndOpenid(Integer id, String openid);

    Drugs findByCode(String code);

    Drugs findByCodeAndOpenid(String code,String openid);
} 