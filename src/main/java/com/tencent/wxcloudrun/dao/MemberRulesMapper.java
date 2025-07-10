package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberRulesMapper {

    MemberRules getRuleByNameAndMid(@Param("name") String name, @Param("mid") Integer mid);
    List<MemberRules> getRuleByNameAndMids(@Param("name") String name, @Param("mid") Integer mid);
    void insertOne(MemberRules memberRules);
    List<MemberRules> getRulesByMid(@Param("mId") Integer mId);
    int delete(@Param("id") Integer id);

    MemberRules getRuleById(@Param("id") Integer id);

    void updateRuleById(MemberRules memberRules);

    List<MemberRules> getActiveRulesByMid(@Param("mId") Integer mId);
    
    /**
     * 统计指定mid的活跃规则数量
     * @param mId 会员ID
     * @return 规则数量
     */
    int countActiveRulesByMid(@Param("mId") Integer mId);

    void swapRuleSort(@Param("currentId") Integer currentId, 
                     @Param("targetId") Integer targetId,
                     @Param("currentSort") Integer currentSort,
                     @Param("targetSort") Integer targetSort);

    MemberRules getLastSortByTypeAndMid(@Param("mId") Integer mid, @Param("type")String type);

    void deleteByMid(@Param("mid") Integer mid);

    void updateStreakById(@Param("id") Integer id, @Param("currentStreak") Integer currentStreak, @Param("longestStreak") Integer longestStreak);

    int updateRuleTypeByMid(@Param("mid") Integer mid, @Param("oldType") String oldType, @Param("newType") String newType);

    List<Map<String, Object>> getRuleTypesByMid(@Param("mid") Integer mid);

    /**
     * 批量更新指定分类的typeSort值
     * @param mid 会员ID
     * @param type 分类名称
     * @param newTypeSort 新的typeSort值
     * @return 影响的行数
     */
    int updateTypeSortByType(@Param("mid") Integer mid, @Param("type") String type, @Param("newTypeSort") Integer newTypeSort);

}
