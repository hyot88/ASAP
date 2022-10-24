package com.fourtwod.web;

import com.fourtwod.config.auth.LoginUser;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.service.user.UserService;
import com.fourtwod.web.dto.UserDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"사용자 API"})
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserApiController {

    private final UserService userService;

    @PatchMapping("/nickname/{flag}")
    @ApiOperation(value = "닉네임 체크/저장", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "유효성 체크(0), 유효성 체크 및 저장(1)", required = true
                    , dataType = "int", paramType = "path", example = "0")
            , @ApiImplicitParam(name = "userDto", value = "사용자 DTO\nnickname: 닉네임", required = true
                    , dataType = "UserDto", paramType = "body")
    })
    public ApiResult checkOrUpdateNickName(@PathVariable int flag, @RequestBody UserDto userDto, @ApiIgnore @LoginUser SessionUser user) throws Exception {
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
