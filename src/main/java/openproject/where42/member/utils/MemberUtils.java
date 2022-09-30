package openproject.where42.member.utils;

import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.Cluster;
import openproject.where42.member.domain.enums.Floor;
import openproject.where42.member.domain.enums.Locate;

import java.util.ArrayList;
import java.util.List;

public class MemberUtils {// 이미 해당 멤버가 그 그룹 이름을 가지고 있는지 확인하는 메서드
    private Member member;
    private int flag;
    private int inOutState;
    private String seat;

    public MemberUtils getMyInfo(Member member) { // 내 상태 조회 메소드
        if (api ok) // api 자리 정보 있으면 -> 무조건 출근
            return getMyAutoInfo(member); // api 정보를 같이 넘겨줄 수 있나?
        else{
            if (42 hane is 출근)
                return getMySelfInfo(member);
            else
                return getMyOutInfo(member);
        }
    }
    private MemberUtils getMyAutoInfo(Member member) {
        this.flag = 0; // 자동 정보 플래그
        this.inOutState = 1; // 출근
        this.seat = "42api 정보"; // string??? 모르겠음
        this.member = member;

        return this; // json으로 넘겨 줄 내용들인데,, 일단 어케하는 지 모르니 이렇게 적는다~
    }

    private MemberUtils getMySelfInfo(Member member) {
        this.flag = 1; // 수동 정보 플래그
        this.inOutState = 1;

        return this; // cluster, floor, locate 정보 여부는 프론트에서 확인해서 처리
    }

    private MemberUtils getMyOutInfo(Member member) {
        this.flag = 1;
        this.inOutState = 0;
        // 외출 관련 로직 추가 여부에 따라 시간 계산 로직 필요
        member.updateLocate(null, null, null);
        this.member = member;
        return this;
    }
}
