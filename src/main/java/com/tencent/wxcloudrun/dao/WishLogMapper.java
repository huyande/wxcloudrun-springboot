package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.WishLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WishLogMapper {
    
    // 根据ID查询
    WishLog getById(@Param("id") Integer id);
    
    // 根据用户ID查询列表
    List<WishLog> getByUid(@Param("uid") String uid);
    
    // 根据愿望ID查询列表
    List<WishLog> getByWid(@Param("wid") Integer wid);
    
    // 新增
    int insert(WishLog wishLog);
    
    // 更新
    void update(WishLog wishLog);
    
    // 删除
    void deleteById(@Param("id") Integer id);

    Integer getSumNumByMid(@Param("mid") Integer mid);

    List<Map<String, Object>> getByMid(@Param("mid")Integer mid);

    List<WishLog> queryTimeTask();

    List<WishLog> getAllLogByMidAndStatus(Integer mid, Integer status);

    void deleteByMid(@Param("mid") Integer mid);
    
    /**
     * 统计指定会员和状态的愿望日志数量
     * @param mid 会员ID
     * @param status 状态，可为null
     * @return 总数
     */
    int getCountByMidAndStatus(@Param("mid") Integer mid, @Param("status") Integer status);
    
    /**
     * 分页查询指定会员和状态的愿望日志
     * @param mid 会员ID
     * @param status 状态，可为null
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 愿望日志列表
     */
    List<Map<String, Object>> getByMidWithPage(@Param("mid") Integer mid, 
                                              @Param("status") Integer status, 
                                              @Param("offset") Integer offset, 
                                              @Param("pageSize") Integer pageSize);
}
