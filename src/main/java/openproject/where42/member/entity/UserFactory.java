package openproject.where42.member.entity;

import openproject.where42.member.entity.enums.MemberLevel;

public class UserFactory {
    public User createUser(MemberLevel memberLevel) {
        User user = null;
        if (memberLevel == MemberLevel.administrator)
            user = new Administrator();
        else if (memberLevel == MemberLevel.member)
            user = new Member();
        return user;
    }
}