package com.tencent.wxcloudrun.service.drugRecord;

import com.tencent.wxcloudrun.model.drugRecord.Tags;
import java.util.List;

public interface TagsService {
    Tags getByIdAndOpenid(Integer id, String openid);
    
    List<Tags> listByOpenid(String openid);
    
    List<Tags> listByDrugId(Integer drugId, String openid);
    
    int insert(Tags tags);
    
    int update(Tags tags);
    
    int deleteByIdAndOpenid(Integer id, String openid);
} 