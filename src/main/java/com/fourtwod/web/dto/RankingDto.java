package com.fourtwod.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class RankingDto {

    private int ranking;
    private int tier;
    private int tierPoint;
    private String nickname;
}
