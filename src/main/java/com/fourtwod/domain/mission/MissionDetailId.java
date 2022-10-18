package com.fourtwod.domain.mission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class MissionDetailId implements Serializable {

    @Column
    @GeneratedValue
    private Long missionDetailId;

    @Column
    private String date;

    @Builder
    public MissionDetailId(Long missionDetailId, String date) {
        this.missionDetailId = missionDetailId;
        this.date = date;
    }
}
