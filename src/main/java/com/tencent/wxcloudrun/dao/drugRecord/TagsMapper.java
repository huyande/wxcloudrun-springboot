package com.tencent.wxcloudrun.dao.drugRecord;

import com.tencent.wxcloudrun.model.drugRecord.Tags;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagsMapper {
    
    @Select("SELECT * FROM tags WHERE id = #{id} AND openid = #{openid}")
    Tags getByIdAndOpenid(@Param("id") Integer id, @Param("openid") String openid);
    
    @Select("SELECT * FROM tags WHERE openid = #{openid}")
    List<Tags> listByOpenid(@Param("openid") String openid);
    
    @Insert("INSERT INTO tags(openid, name) VALUES(#{openid}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tags tags);
    
    @Update("UPDATE tags SET name = #{name} WHERE id = #{id} AND openid = #{openid}")
    int update(Tags tags);
    
    @Delete("DELETE FROM tags WHERE id = #{id} AND openid = #{openid}")
    int deleteByIdAndOpenid(@Param("id") Integer id, @Param("openid") String openid);
    
    @Select("SELECT t.* FROM tags t " +
            "INNER JOIN tag_drug_relas r ON t.id = r.tid " +
            "WHERE r.did = #{drugId} AND t.openid = #{openid}")
    List<Tags> listByDrugId(@Param("drugId") Integer drugId, @Param("openid") String openid);
} 