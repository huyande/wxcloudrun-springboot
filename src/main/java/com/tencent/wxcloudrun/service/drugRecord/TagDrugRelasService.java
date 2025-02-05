package com.tencent.wxcloudrun.service.drugRecord;

import com.tencent.wxcloudrun.model.drugRecord.TagDrugRelas;
import java.util.List;

public interface TagDrugRelasService {
    List<TagDrugRelas> listByDrugId(Integer drugId);
    
    int addTagToDrug(Integer drugId, Integer tagId);
    
    int removeTagFromDrug(Integer drugId, Integer tagId);
    
    int removeAllTagsFromDrug(Integer drugId);
    
    int removeAllDrugsFromTag(Integer tagId);
} 