package com.fourtwod.config.auth.dto;

import com.fourtwod.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String nickname;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.picture = user.getPicture();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
