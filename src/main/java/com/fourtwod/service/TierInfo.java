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
public enum TierInfo {

    브론즈4(0, 0, 99, 1f),
    브론즈3(1, 100, 199, 1f),
    브론즈2(2, 200, 299, 1f),
    브론즈1(3, 300, 399, 1f),

    실버4(4, 400, 499, 1.5f),
    실버3(5, 500, 599, 1.5f),
    실버2(6, 600, 699, 1.5f),
    실버1(7, 700, 799, 1.5f),

    골드4(8, 800, 899, 2f),
    골드3(9, 900, 999, 2),
    골드2(10, 1000, 1099, 2f),
    골드1(11, 1100, 1199, 2f);

    private final int tier;
    private final int tierPointStart;
    private final int tierPointEnd;
    private final float penalty;

    private static final Map<Integer, TierInfo> codes = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(TierInfo::getTier, Function.identity())));

    public static TierInfo find(int tier) {
        return Optional.ofNullable(codes.get(tier)).orElse(null);
    }
}