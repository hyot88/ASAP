package com.fourtwod.domain.user;

import com.fourtwod.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@ToString
public class User extends BaseTimeEntity {

    @EmbeddedId
    private UserId userId;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    @Column(nullable = false)
    private int tier;

    @Column(nullable = false)
    private int tierPoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(UserId userId, String name, String nickname, int tier, int tierPoint, Role role) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.tier = tier;
        this.tierPoint = tierPoint;
        this.role = role;
    }

    public User updateName(String name) {
        this.name = name;
        return this;
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }


}
