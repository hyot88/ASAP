package com.fourtwod.domain.mission;

import com.fourtwod.domain.BaseTimeEntity;
import com.fourtwod.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@ToString
@SequenceGenerator(
        name = "MISSION_SEQ_GENERATOR"
        , sequenceName = "MISSION_SEQ"
        , allocationSize = 1
)
public class Mission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MISSION_SEQ_GENERATOR")
    private Long missionId;

    @Column(nullable = false)
    private int missionType;

    @Column(nullable = false)
    private int proceeding;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "email")
            , @JoinColumn(name = "registration_id")})
    private User user;

    @Builder
    public Mission(Long missionId, int missionType, int proceeding, User user) {
        this.missionId = missionId;
        this.missionType = missionType;
        this.proceeding = proceeding;
        this.user = user;
    }
}
