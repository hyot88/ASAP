package com.fourtwod.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String name;
    private String nickname;
    private int tier;
    private int tierPoint;
}
