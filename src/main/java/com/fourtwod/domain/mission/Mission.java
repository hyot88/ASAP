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
    private String date;

    @Column(nullable = false)
    private int proceeding;

    @Column(nullable = false)
    private int successFlag;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "email")
            , @JoinColumn(name = "registration_id")})
    private User user;

    @OneToOne(mappedBy = "mission")
    private MissionHistory missionHistory;

    @Builder
    public Mission(Long missionId, int missionType, String date, int proceeding, int successFlag, User user) {
        this.missionId = missionId;
        this.missionType = missionType;
        this.date = date;
        this.proceeding = proceeding;
        this.successFlag = successFlag;
        this.user = user;
    }
}
