package com.fourtwod.domain.mission;

import com.fourtwod.domain.BaseTimeEntity;
import com.fourtwod.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Mission extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long missionId;

    @Column(nullable = false)
    private int missionType;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "email")
            , @JoinColumn(name = "registration_id")})
    private User user;

    @Builder
    public Mission(Long missionId, int missionType, User user) {
        this.missionId = missionId;
        this.missionType = missionType;
        this.user = user;
    }
}
