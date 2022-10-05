package com.fourtwod.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class UserDto {

    private String email;
    private String registrationId;
    private String name;
    private String nickname;
}
