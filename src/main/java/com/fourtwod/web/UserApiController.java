package com.fourtwod.web;

import com.fourtwod.config.auth.LoginUser;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.service.user.UserService;
import com.fourtwod.web.dto.UserDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserApiController {

    private final UserService userService;

    @PatchMapping("/user/nickname/{flag}")
    public ApiResult checkOrUpdateNickName(@PathVariable int flag, @RequestBody UserDto userDto, @LoginUser SessionUser user) throws Exception {
        if (user != null ) {
            ResponseCode responseCode = userService.checkOrUpdateNickName(flag, userDto.getNickname(), user);
            return new ApiResult<>(responseCode);
        } else {
            throw new Exception();
        }
    }

    @GetMapping("/nmReset")
    public ApiResult nmReset() throws Exception {
        return new ApiResult<>(userService.deleteAll());
    }
}
