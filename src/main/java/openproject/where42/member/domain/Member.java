package openproject.where42.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.member.domain.enums.MemberLevel;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends User {
    @Enumerated
    private MemberLevel level;

    private String msg;

    private Long defaultGroupId;

    private Long starredGroupId;

    private String img;

    @Embedded
    private Locate locate = new Locate(null, -1, -1, null);

    public Member(String name, String img, MemberLevel level) {
        this.name = name;
        this.img = img;
        this.level = level;
    }
    public void updatePersonalMsg(String msg) {
        this.msg = msg;
    }

    public void setDefaultGroup(Long defaultGroupId, Long starredGroupId) {
        this.defaultGroupId = defaultGroupId;
        this.starredGroupId = starredGroupId;
    }
}