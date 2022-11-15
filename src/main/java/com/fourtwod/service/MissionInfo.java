package com.fourtwod.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum MissionInfo {

    Tick_1Day(1, 10),
    Tick_3Day(3, 60),
    Quick_5Day(5, 150),
    Quick_7Day(7, 280),
    Huick_15Day(15, 100),
    Huick_30Day(30, 300);

    private final int missionType;
    private final int rewardPoint;

    private static final Map<Integer, MissionInfo> codes = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(MissionInfo::getMissionType, Function.identity())));

    public static MissionInfo find(int missionType) {
        return Optional.ofNullable(codes.get(missionType)).orElse(null);
    }
}
