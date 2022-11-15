package com.fourtwod.domain.mission;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionDetailRepository extends JpaRepository<MissionDetail, MissionDetailId> {
}
