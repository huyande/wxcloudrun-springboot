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

    /**
     * 获取指定时间范围内的心愿兑换总次数
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 兑换总次数
     */
    Integer getExchangeCountByDateRange(@Param("mid") Integer mid, 
                                       @Param("startDate") String startDate, 
                                       @Param("endDate") String endDate);
    
    /**
     * 获取指定时间范围内的积分消耗总数
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 积分消耗总数
     */
    Integer getConsumedPointsByDateRange(@Param("mid") Integer mid, 
                                        @Param("startDate") String startDate, 
                                        @Param("endDate") String endDate);
    
    /**
     * 获取指定时间范围内兑换次数前N的心愿
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制返回的心愿数量
     * @return 心愿列表，包含心愿名称和兑换次数
     */
    List<Map<String, Object>> getTopWishesByDateRange(@Param("mid") Integer mid, 
                                                    @Param("startDate") String startDate, 
                                                    @Param("endDate") String endDate,
                                                    @Param("limit") Integer limit);
    
    /**
     * 按月份获取心愿兑换消耗的积分
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月份积分消耗列表，包含月份和对应的积分消耗
     */
    List<Map<String, Object>> getWishConsumedPointsByMonth(@Param("mid") Integer mid, 
                                                         @Param("startDate") String startDate, 
                                                         @Param("endDate") String endDate);
}
