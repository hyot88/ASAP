package com.fourtwod.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndRegistrationId(String email, String registrationId);

    Optional<User> findByNickname(String nickname);

    void deleteAll();
}
