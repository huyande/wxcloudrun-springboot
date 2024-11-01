package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.MemberRelas;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberRelasMapper {
    void insertOne(MemberRelas memberRelas);

    MemberRelas getOneByMidAndUid(@Param("mid") Integer mid, @Param("uid") Integer uid);

    void deleteByMidAndUid(@Param("mid") Integer mid, @Param("uid") Integer uid);
}
