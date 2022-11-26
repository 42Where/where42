package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.member.entity.enums.MemberLevel;

import javax.persistence.*;
import java.util.Date;

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
    private int inOrOut;
    @Embedded
    private Locate locate = new Locate(null, 0, 0, null);
    private String location;
    @Temporal(TemporalType.TIMESTAMP)
    Date updateTime;
    @Temporal(TemporalType.TIMESTAMP)
    Date createTime;


    public Member(String name, String img, String location, MemberLevel level) {
        this.name = name;
        this.img = img;
        this.level = level;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    public void updatePersonalMsg(String msg) {
        this.msg = msg;
    }

    public void setDefaultGroup(Long defaultGroupId, Long starredGroupId) {
        this.defaultGroupId = defaultGroupId;
        this.starredGroupId = starredGroupId;
    }

    public void updateLocation(String location) {
        this.location = location;
        this.updateTime = new Date();
    }

    public void updateInOrOut(int inOrOUt) {
        this.inOrOut = inOrOUt;
        this.location = Define.PARSED;
    }

    public void updateStatus(int inOrOut) {
        this.inOrOut = inOrOut;
        this.location = Define.PARSED;
        this.updateTime = new Date();
    }

    public Long timeDiff() {
        Date now = new Date();
        return (now.getTime() - updateTime.getTime())/ 60000;
    }
}