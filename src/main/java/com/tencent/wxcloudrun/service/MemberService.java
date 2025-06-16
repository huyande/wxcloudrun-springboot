package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;

import java.util.List;
import java.util.Map;

public interface MemberService {
    List<Member> getMembersByFamilyCode(String FamilyCode);
    Integer getMemberCountByUid(Integer uId);
    Member insert(MemberRequest memberRequest);

    List<Member> getMembersByUid(Integer id);

    Member getMemberById(Integer mid);

    Member updateMember(Integer id, MemberRequest memberRequest);

    void clearMemberData(Integer mid);

    void deleteMemberById(Integer id);
    
    /**
     * 切换成员的模式（常规/赛季）
     * @param mid 成员ID
     * @param mode 模式类型，"NORMAL"表示常规模式，"SEASON"表示赛季模式
     * @return 更新后的成员信息
     */
    Member switchMode(Integer mid, String mode);
    
    /**
     * 更新成员关联的赛季ID
     * @param mid 成员ID
     * @param seasonId 赛季ID
     * @return 更新后的成员信息
     */
    Member updateCurrentSeasonId(Integer mid, Long seasonId);

    /**
     * 获取家庭组中其他成员的规则列表
     * @param mid 当前成员ID
     * @param familyCode 家庭代码
     * @return 其他成员及其规则列表
     */
    List<Map<String, Object>> getFamilyMembersRules(Integer mid, String familyCode);

    /**
     * 批量导入规则
     * @param mid 目标成员ID
     * @param ruleIds 要导入的规则ID列表
     * @return 导入结果
     */
    Map<String, Object> batchImportRules(Integer mid, List<Integer> ruleIds);
}
