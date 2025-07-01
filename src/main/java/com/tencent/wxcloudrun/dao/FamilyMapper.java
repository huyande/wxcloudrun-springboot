package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Family;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FamilyMapper {

    List<Family> getUidsByCode(@Param("code") String code);

    int delete(@Param("id") Integer id);

    Family getOneByCodeAndUid(@Param("code") String code, @Param("uid") Integer uid);

    void insertOne(Family family);

    void deleteByCodeAndUid(@Param("code") String code, @Param("uid") Integer uid);

    void updateIsReadByCodeAndUid(@Param("code") String code, @Param("uid") Integer uid, @Param("isRead") Integer isRead);

    Family findByCodeAndBindMid(@Param("code") String code, @Param("bindMid") Integer bindMid);

    void updateChildModeById(@Param("id") Integer id, @Param("childMode") String childMode);
}
