package openproject.where42.member.domain;

import openproject.where42.group.domain.Groups;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.domain.enums.Place;
import openproject.where42.member.domain.enums.MemberLevel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class Administrator extends User {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "owner")
    List<Groups> groups = new ArrayList<>();

    private String msg;

    @Enumerated
    private MemberLevel level = MemberLevel.administrator;

    @Enumerated
    private Planet planet;

    private int floor;

    private int cluster;

    @Enumerated
    private Place place;
}
