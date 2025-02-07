package com.tencent.wxcloudrun.service.impl.drugRecord;

import com.tencent.wxcloudrun.dao.drugRecord.DrugsMapper;
import com.tencent.wxcloudrun.dto.PageResponse;
import com.tencent.wxcloudrun.model.drugRecord.Drugs;
import com.tencent.wxcloudrun.service.drugRecord.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugsServiceImpl implements DrugsService {
    
    @Autowired
    private DrugsMapper drugsMapper;
    
    @Override
    public Drugs getByIdAndOpenid(Integer id, String openid) {
        return drugsMapper.getByIdAndOpenid(id, openid);
    }
    
    @Override
    public PageResponse<Drugs> listByCondition(String openid, String name, String code, Integer pageNum, Integer pageSize) {
        // 获取总数
        int total = drugsMapper.countByCondition(openid, name, code);
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询数据
        List<Drugs> list = drugsMapper.listByCondition(openid, name, code, offset, pageSize);
        
        // 返回分页结果
        return PageResponse.of(list, total, pageNum, pageSize);
    }
    
    @Override
    public int insert(Drugs drugs) {
        return drugsMapper.insert(drugs);
    }
    
    @Override
    public int update(Drugs drugs) {
        return drugsMapper.update(drugs);
    }
    
    @Override
    public int deleteByIdAndOpenid(Integer id, String openid) {
        return drugsMapper.deleteByIdAndOpenid(id, openid);
    }

    @Override
    public Drugs findByCode(String code) {
        return drugsMapper.findByCode(code);
    }

    @Override
    public Drugs findByCodeAndOpenid(String code, String openid) {
        return drugsMapper.findByCodeAndOpenid(code,openid);
    }
} 