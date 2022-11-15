package com.fourtwod.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class MissionHistoryDto {

    private String date;
    private String missionType;
    private int tookCount;
    private int changeTierPoint;
}
