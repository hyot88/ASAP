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
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class UserService {

    private final HttpSession httpSession;
    private final UserRepository userRepository;

    @Transactional
    public ResponseCode updateCheck(String email, String registrationId, UserSaveRequestDto userSaveRequestDto) throws Exception {
        User userByEmail = userRepository.findByUserId(UserId.builder().email(email).registrationId(registrationId).build()).orElse(null);

        // Email이 검색되지 않을 경우
        if (userByEmail == null) {
            return ResponseCode.COMM_E001;
        }

        String nickname = userSaveRequestDto.getNickname();

        // 2자 이상 10자 이하 닉네임이 아닌 경우
        if (nickname.length() < 2 || nickname.length() > 10) {
            return ResponseCode.NICK_E001;
        }

        // 한글,영문,숫자 닉네임이 아닌 경우
        if (!Pattern.matches("^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$", nickname)) {
            return ResponseCode.NICK_E002;
        }

        User userByNickname = userRepository.findByNickname(nickname).orElse(null);

        // 닉네임이 중복될 경우
        if (userByNickname != null) {
            return ResponseCode.NICK_E003;
        }

        return ResponseCode.COMM_S000;
    }

    // TODO: 중복코드 제거 필요함
    @Transactional
    public ResponseCode update(String email, String registrationId, UserSaveRequestDto userSaveRequestDto) throws Exception {
        User userByEmail = userRepository.findByUserId(UserId.builder().email(email).registrationId(registrationId).build()).orElse(null);

        // Email이 검색되지 않을 경우
        if (userByEmail == null) {
            return ResponseCode.COMM_E001;
        }

        String nickname = userSaveRequestDto.getNickname();

        // 2자 이상 10자 이하 닉네임이 아닌 경우
        if (nickname.length() < 2 || nickname.length() > 10) {
            return ResponseCode.NICK_E001;
        }

        // 한글,영문,숫자 닉네임이 아닌 경우
        if (!Pattern.matches("^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$", nickname)) {
            return ResponseCode.NICK_E002;
        }

        User userByNickname = userRepository.findByNickname(nickname).orElse(null);

        // 닉네임이 중복될 경우
        if (userByNickname != null) {
            return ResponseCode.NICK_E003;
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
