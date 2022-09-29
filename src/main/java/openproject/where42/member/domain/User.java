package openproject.where42.member.domain;

import javax.persistence.*;

@MappedSuperclass
@SequenceGenerator(
        name = "MEMBERS_SEQ_GENERATOR",
        sequenceName = "MEMBERS_SEQ",
        initialValue = 1, allocationSize = 1
)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBERS_SEQ_GENERATOR")
    @Column(name = "member_id")
    protected Long id;

    @Column(name = "member_name", nullable = false, unique = true)
    protected String name;
}
