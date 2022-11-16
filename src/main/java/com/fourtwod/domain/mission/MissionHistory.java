package com.fourtwod.domain.mission;

import com.fourtwod.domain.BaseTimeEntity;
import com.fourtwod.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class MissionHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long missionHistoryId;

    @Column(nullable = false)
    private int missionType;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private int successFlag;

    @Column(nullable = false)
    private int changeTierPoint;

    @Column(nullable = false)
    private int tookCount;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "email")
            , @JoinColumn(name = "registration_id")})
    private User user;
}
