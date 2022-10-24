package com.fourtwod.service.mission;

import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.mission.MissionDetailRepository;
import com.fourtwod.domain.mission.MissionRepository;
import com.fourtwod.web.handler.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionDetailRepository missionDetailRepository;

    @Transactional
    public ResponseCode selectMissionInProgress(SessionUser user) {
        return ResponseCode.COMM_S000;
    }
}
