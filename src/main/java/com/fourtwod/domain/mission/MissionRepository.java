package com.fourtwod.domain.mission;

import com.fourtwod.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Mission> findByUser(User user);
}
