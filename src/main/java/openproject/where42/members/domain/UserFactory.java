package openproject.where42.members.domain;

import openproject.where42.members.domain.enums.MemberLevel;

public class UserFactory {
    public Users createUser(MemberLevel memberLevel) {
        Users user = null;
        if (memberLevel == MemberLevel.administrator)
            user = new Administrators();
        else if (memberLevel == MemberLevel.member)
            user = new Members();
    }
}