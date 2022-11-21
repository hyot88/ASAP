package com.fourtwod.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class MissionDto {

    private int missionType;
    private List<Detail> detail;

    @Getter
    @Builder
    @ToString
    public static class Detail {
        String date;
        int afternoon;
        int night;
    }
}
