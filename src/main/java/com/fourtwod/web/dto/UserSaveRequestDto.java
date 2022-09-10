package com.fourtwod.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

    private String nickname;

    @Builder
    public UserSaveRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
