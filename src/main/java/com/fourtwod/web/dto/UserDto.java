package com.fourtwod.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserDto {

    private String email;
    private String registrationId;
    private String name;
    private String nickname;
}
