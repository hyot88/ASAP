package com.fourtwod.service.user;

import com.fourtwod.config.auth.dto.SessionUser;
import com.fourtwod.domain.user.User;
import com.fourtwod.domain.user.UserId;
import com.fourtwod.domain.user.UserRepository;
import com.fourtwod.web.dto.UserSaveRequestDto;
import com.fourtwod.web.handler.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class UserService {

    private final HttpSession httpSession;
    private final UserRepository userRepository;

    @Transactional
    public ResponseCode update(String email, String registrationId, UserSaveRequestDto userSaveRequestDto) throws Exception {
        User userByEmail = userRepository.findByUserId(UserId.builder().email(email).registrationId(registrationId).build()).orElse(null);

        // Email이 검색되지 않을 경우
        if (userByEmail == null) {
            return ResponseCode.COMM_E001;
        }
        
        String nickname = userSaveRequestDto.getNickname();
        User userByNickname = userRepository.findByNickname(nickname).orElse(null);

        // 닉네임이 중복될 경우
        if (userByNickname != null) {
            return ResponseCode.COMM_E002;
        }

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        sessionUser.setNickname(nickname);

        httpSession.setAttribute("user", sessionUser);
        userByEmail.updateNickname(nickname);

        return ResponseCode.COMM_S000;
    }

    @Transactional
    public ResponseCode deleteAll() throws Exception {
        userRepository.deleteAll();

        return ResponseCode.COMM_S000;
    }
}
