package com.fourtwod.web;

import com.fourtwod.config.auth.LoginUser;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.service.mission.MissionService;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"미션 API"})
@RequiredArgsConstructor
@RequestMapping("/api/mission")
@RestController
public class MissionApiController {

    private final MissionService missionService;

    @GetMapping("")
    @ApiOperation(value = "진행중인 미션 조회", response = ApiResult.class)
    public ApiResult selectMissionInProgress(@ApiIgnore @LoginUser SessionUser user) throws Exception {
        if (user != null ) {
            ResponseCode responseCode = missionService.selectMissionInProgress(user);
            return new ApiResult<>(responseCode);
        } else {
            throw new Exception();
        }
    }
}
