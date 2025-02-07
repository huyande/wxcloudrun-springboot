package com.tencent.wxcloudrun.service.impl.drugRecord;

import com.tencent.wxcloudrun.dao.drugRecord.TagsMapper;
import com.tencent.wxcloudrun.model.drugRecord.Tags;
import com.tencent.wxcloudrun.service.drugRecord.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagsServiceImpl implements TagsService {
    
    @Autowired
    private TagsMapper tagsMapper;
    
    @Override
    public Tags getByIdAndOpenid(Integer id, String openid) {
        return tagsMapper.getByIdAndOpenid(id, openid);
    }
    
    @Override
    public List<Tags> listByOpenid(String openid) {
        return tagsMapper.listByOpenid(openid);
    }
    
    @Override
    public List<Tags> listByDrugId(Integer drugId, String openid) {
        return tagsMapper.listByDrugId(drugId, openid);
    }
    
    @Override
    public int insert(Tags tags) {
        return tagsMapper.insert(tags);
    }
    
    @Override
    public int update(Tags tags) {
        return tagsMapper.update(tags);
    }
    
    @Override
    public int deleteByIdAndOpenid(Integer id, String openid) {
        return tagsMapper.deleteByIdAndOpenid(id, openid);
    }
} 