package com.fourtwod.domain.mission;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
@ToString
public class MissionDetailId implements Serializable {

    @Column
    private Long missionDetailId;

    @Column
    private String date;

    @Builder
    public MissionDetailId(Long missionDetailId, String date) {
        this.missionDetailId = missionDetailId;
        this.date = date;
    }
}
