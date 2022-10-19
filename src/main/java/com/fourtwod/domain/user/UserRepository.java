package com.fourtwod.domain.user;

import com.fourtwod.domain.mission.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {

    Optional<User> findByUserId(UserId userId);

    Optional<User> findByNickname(String nickname);

    void deleteAll();
}
