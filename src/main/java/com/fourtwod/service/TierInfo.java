package com.fourtwod.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TierInfo {

    unranked(-1, 0, 0, 0),
    bronze4(0, 0, 99, 1f),
    bronze3(1, 100, 199, 1f),
    bronze2(2, 200, 299, 1f),
    bronze1(3, 300, 399, 1f),

    silver4(4, 400, 499, 1.5f),
    silver3(5, 500, 599, 1.5f),
    silver2(6, 600, 699, 1.5f),
    silver1(7, 700, 799, 1.5f),

    gold4(8, 800, 899, 2f),
    gold3(9, 900, 999, 2),
    gold2(10, 1000, 1099, 2f),
    gold1(11, 1100, 1199, 2f),

    platinum4(12, 1200, 1299, 2.5f),
    platinum3(13, 1300, 1399, 2.5f),
    platinum2(14, 1400, 1499, 2.5f),
    platinum1(15, 1500, 1599, 2.5f),

    diamond4(16, 1600, 1699, 3f),
    diamond3(17, 1700, 1799, 3f),
    diamond2(18, 1800, 1899, 3f),
    diamond1(19, 1900, 1999, 3f),

    master3(20, 2000, 2099, 3.5f),
    master2(21, 2100, 2199, 3.5f),
    master1(22, 2200, 2299, 3.5f);

    private final int tier;
    private final int tierPointStart;
    private final int tierPointEnd;
    private final float penalty;

    private static final Map<Integer, TierInfo> codes = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(TierInfo::getTier, Function.identity())));

    public static TierInfo findByTier(int tier) {
        return Optional.ofNullable(codes.get(tier)).orElse(TierInfo.unranked);
    }

    public static TierInfo findByTierPoint(int tierPoint) {
        AtomicReference<TierInfo> tierInfo = new AtomicReference<>(TierInfo.unranked);
        codes.forEach((tier, tmpTierInfo) -> {
            if (tierPoint >= tmpTierInfo.getTierPointStart() && tierPoint <= tmpTierInfo.getTierPointEnd()) {
                tierInfo.set(tmpTierInfo);
            }
        });

        return tierInfo.get();
    }
}
