package openproject.where42.member.domain;

import openproject.where42.member.domain.enums.MemberLevel;

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