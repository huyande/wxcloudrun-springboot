package com.tencent.wxcloudrun.dao.drugRecord;

import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.drugRecord.WxUsers;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WxUsersMapper {
    
    @Select("SELECT * FROM wx_users WHERE id = #{id}")
    WxUsers getById(@Param("id") Integer id);
    
    @Select("SELECT * FROM wx_users WHERE openid = #{openid}")
    WxUsers getByOpenid(@Param("openid") String openid);
    
    @Select("SELECT * FROM wx_users")
    List<WxUsers> getAll();
    
    @Insert("INSERT INTO wx_users(openid) VALUES(#{openid})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WxUsers wxUsers);
    
    @Update("UPDATE wx_users SET openid = #{openid} WHERE id = #{id}")
    int update(WxUsers wxUsers);
    
    @Delete("DELETE FROM wx_users WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT * FROM wx_check_config where version= #{version}")
    WxCheckConfig getWxCheckConfig(@Param("version")String version);
}