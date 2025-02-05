package com.tencent.wxcloudrun.dao.drugRecord;

import com.tencent.wxcloudrun.model.drugRecord.TagDrugRelas;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagDrugRelasMapper {
    
    @Select("SELECT * FROM tag_drug_relas WHERE did = #{drugId}")
    List<TagDrugRelas> listByDrugId(@Param("drugId") Integer drugId);
    
    @Insert("INSERT INTO tag_drug_relas(did, tid) VALUES(#{did}, #{tid})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TagDrugRelas tagDrugRelas);
    
    @Delete("DELETE FROM tag_drug_relas WHERE did = #{drugId} AND tid = #{tagId}")
    int deleteByDrugIdAndTagId(@Param("drugId") Integer drugId, @Param("tagId") Integer tagId);
    
    @Delete("DELETE FROM tag_drug_relas WHERE did = #{drugId}")
    int deleteByDrugId(@Param("drugId") Integer drugId);
    
    @Delete("DELETE FROM tag_drug_relas WHERE tid = #{tagId}")
    int deleteByTagId(@Param("tagId") Integer tagId);
} 