package com.fourtwod.web;

import com.fourtwod.config.auth.LoginUser;
import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.MissionDetail;
import com.fourtwod.service.mission.MissionService;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

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
            List<MissionDetail> list = missionService.selectMissionInProgress(user);
            return new ApiResult<>(list);
        } else {
            throw new Exception();
        }
    }

    @PostMapping("/{missionType}")
    @ApiOperation(value = "미션 생성", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "missionType", value = "1: 1 Day\n3: 3 Day\n5: 5 Day\n7: 7 Day", required = true
                    , dataType = "int", paramType = "path", example = "1")
    })
    public ApiResult createMission(@PathVariable int missionType, @ApiIgnore @LoginUser SessionUser user) throws Exception {
        if (user != null ) {
            ResponseCode responseCode = missionService.createMission(missionType, user);
            return new ApiResult<>(responseCode);
        } else {
            throw new Exception();
        }
    }
}
