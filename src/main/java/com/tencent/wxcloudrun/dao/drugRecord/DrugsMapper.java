package com.tencent.wxcloudrun.dao.drugRecord;

import com.tencent.wxcloudrun.model.drugRecord.Drugs;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DrugsMapper {
    
    @Select("SELECT * FROM drugs WHERE id = #{id} AND openid = #{openid}")
    Drugs getByIdAndOpenid(@Param("id") Integer id, @Param("openid") String openid);
    
    @Select("<script>" +
            "SELECT * FROM drugs WHERE openid = #{openid} " +
            "<if test='name != null and name != \"\"'>" +
            "   AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='code != null and code != \"\"'>" +
            "   AND code = #{code} " +
            "</if>" +
            "ORDER BY createdAt DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Drugs> listByCondition(@Param("openid") String openid, 
                               @Param("name") String name,
                               @Param("code") String code,
                               @Param("offset") Integer offset,
                               @Param("pageSize") Integer pageSize);
    
    @Select("<script>" +
            "SELECT COUNT(*) FROM drugs WHERE openid = #{openid} " +
            "<if test='name != null and name != \"\"'>" +
            "   AND name LIKE CONCAT('%', #{name}, '%') " +
            "</if>" +
            "<if test='code != null and code != \"\"'>" +
            "   AND code = #{code} " +
            "</if>" +
            "</script>")
    int countByCondition(@Param("openid") String openid,
                        @Param("name") String name,
                        @Param("code") String code);
    
    @Insert("INSERT INTO drugs(openid, manuName, img, code, spec, name) " +
            "VALUES(#{openid}, #{manuName}, #{img}, #{code}, #{spec}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Drugs drugs);
    
    @Update("UPDATE drugs SET manuName = #{manuName}, img = #{img}, code = #{code}, " +
            "spec = #{spec}, name = #{name} WHERE id = #{id} AND openid = #{openid}")
    int update(Drugs drugs);
    
    @Delete("DELETE FROM drugs WHERE id = #{id} AND openid = #{openid}")
    int deleteByIdAndOpenid(@Param("id") Integer id, @Param("openid") String openid);

    @Select("SELECT * FROM drugs WHERE code = #{code} limit 1")
    Drugs findByCode(@Param("code") String code);

    @Select("SELECT * FROM drugs WHERE code = #{code} and openid = #{openid}")
    Drugs findByCodeAndOpenid(@Param("code") String code, @Param("openid")  String openid);
}