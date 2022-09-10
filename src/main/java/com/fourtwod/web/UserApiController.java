package com.fourtwod.web;

import com.fourtwod.config.auth.LoginUser;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.service.user.UserService;
import com.fourtwod.web.dto.UserSaveRequestDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/api/nickname")
    public ApiResult nickname(@RequestBody UserSaveRequestDto userSaveRequestDto, @LoginUser SessionUser user) throws Exception {
        if (user != null ) {
            ResponseCode responseCode = userService.update(user.getEmail(), userSaveRequestDto);
            return new ApiResult<>(responseCode);
        } else {
            throw new Exception();
        }
    }
}
