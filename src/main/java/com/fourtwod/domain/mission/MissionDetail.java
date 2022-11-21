package com.fourtwod.domain.mission;

import com.fourtwod.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@ToString
public class MissionDetail extends BaseTimeEntity {

    @EmbeddedId
    private MissionDetailId missionDetailId;

    @Column(nullable = false)
    private int afternoon;

    @Column(nullable = false)
    private int night;

    @ManyToOne
    @MapsId("missionDetailId")
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Builder
    public MissionDetail(MissionDetailId missionDetailId, int afternoon, int night, Mission mission) {
        this.missionDetailId = missionDetailId;
        this.afternoon = afternoon;
        this.night = night;
        this.mission = mission;
    }
}
