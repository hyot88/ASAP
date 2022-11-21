package com.fourtwod.domain.mission;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
