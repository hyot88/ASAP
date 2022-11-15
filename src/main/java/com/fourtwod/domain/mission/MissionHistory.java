package com.fourtwod.domain.mission;

import com.fourtwod.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class MissionHistory extends BaseTimeEntity {

    @Id
    private long missionId;

    @Column(nullable = false)
    private int missionType;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private int successFlag;

    @Column(nullable = false)
    private int changeTierPoint;

    @Column(nullable = false)
    private int tookCount;

    @OneToOne
    @MapsId("missionId")
    @JoinColumn(name = "mission_id")
    private Mission mission;
}
