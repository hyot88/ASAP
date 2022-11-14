package com.fourtwod.domain.mission;

import com.fourtwod.domain.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@ToString
public class MissionHistory extends BaseTimeEntity {

    @Id
    private long missionId;

    @Column(nullable = false)
    private String endDate;

    @Column(nullable = false)
    private int successFlag;

    @Column(nullable = false)
    private int changeTierPoint;

    @Column(nullable = false)
    private int tier;

    @Column(nullable = false)
    private int tierPoint;

    @OneToOne
    @MapsId("missionId")
    @JoinColumn(name = "mission_id")
    private Mission mission;
}
