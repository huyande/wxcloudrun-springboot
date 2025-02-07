package com.tencent.wxcloudrun.service.impl.drugRecord;

import com.tencent.wxcloudrun.dao.drugRecord.TagDrugRelasMapper;
import com.tencent.wxcloudrun.model.drugRecord.TagDrugRelas;
import com.tencent.wxcloudrun.service.drugRecord.TagDrugRelasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagDrugRelasServiceImpl implements TagDrugRelasService {
    
    @Autowired
    private TagDrugRelasMapper tagDrugRelasMapper;
    
    @Override
    public List<TagDrugRelas> listByDrugId(Integer drugId) {
        return tagDrugRelasMapper.listByDrugId(drugId);
    }
    
    @Override
    public int addTagToDrug(Integer drugId, Integer tagId) {
        TagDrugRelas relas = new TagDrugRelas();
        relas.setDid(drugId);
        relas.setTid(tagId);
        return tagDrugRelasMapper.insert(relas);
    }
    
    @Override
    public int removeTagFromDrug(Integer drugId, Integer tagId) {
        return tagDrugRelasMapper.deleteByDrugIdAndTagId(drugId, tagId);
    }
    
    @Override
    public int removeAllTagsFromDrug(Integer drugId) {
        return tagDrugRelasMapper.deleteByDrugId(drugId);
    }
    
    @Override
    public int removeAllDrugsFromTag(Integer tagId) {
        return tagDrugRelasMapper.deleteByTagId(tagId);
    }
} 