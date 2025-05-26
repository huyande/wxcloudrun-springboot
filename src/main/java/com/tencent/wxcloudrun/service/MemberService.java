package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;

import java.util.List;

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
}
