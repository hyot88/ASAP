# 🚀 ASAP - 습관 형성 미션 서비스

## 📋 목차

1. [프로젝트 개요](#-프로젝트-개요)
2. [시스템 아키텍처](#-시스템-아키텍처)
3. [기술 스택](#-기술-스택)
4. [핵심 기능 상세](#-핵심-기능-상세)
5. [도메인 모델](#-도메인-모델)
6. [API 명세](#-api-명세)
7. [배포 구조](#-배포-구조)
8. [실행 방법](#-실행-방법)

---

## 🎯 프로젝트 개요

### 시나리오

> "사용자가 하루 두 번(오전/오후) 'Took'을 던지며 미션을 수행하고, 성공 시 티어 포인트를 획득하여 랭킹에 도전하는 습관 형성 서비스"

ASAP은 **게이미피케이션 기반의 습관 형성 플랫폼**입니다. 사용자는 1일/3일/5일/7일 단위의 미션을 선택하여 참여하고, 정해진 시간대에 출석 체크(Took)를 완료하면 티어 포인트를 획득합니다.

### 핵심 설계 원칙

| 원칙 | 설명 | 적용 기술 |
|------|------|----------|
| **소셜 로그인** | 간편한 회원가입/로그인 | OAuth2 (Google, Naver, Kakao) |
| **게이미피케이션** | 티어/랭킹 시스템으로 동기부여 | 7단계 티어, 실시간 랭킹 |
| **스케줄 기반 정산** | 자동화된 미션 성공/실패 판정 | Spring Scheduler |
| **무중단 배포** | 서비스 중단 없는 배포 | NGINX, Blue-Green 배포 |
| **타입 안전 쿼리** | 컴파일 타임 쿼리 검증 | QueryDSL |

---

## 🏗️ 시스템 아키텍처

### 전체 구조

```
┌─────────────────────────────────────────────────────────────────────┐
│                              Client                                  │
│                    (Web Browser / Mobile)                           │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         NGINX (Reverse Proxy)                        │
│              - Load Balancing (Port 8081 ↔ 8082)                    │
│              - Blue-Green 배포                                       │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
         ┌──────────────────┐        ┌──────────────────┐
         │   Spring Boot    │        │   Spring Boot    │
         │   (real1:8081)   │        │   (real2:8082)   │
         └────────┬─────────┘        └────────┬─────────┘
                  │                           │
                  └─────────────┬─────────────┘
                                │
              ┌─────────────────┼─────────────────┐
              ▼                 ▼                 ▼
     ┌────────────────┐  ┌────────────┐  ┌───────────────┐
     │  OAuth2 Server │  │  MariaDB   │  │    Jenkins    │
     │ (Google/Naver/ │  │   (RDS)    │  │   (CI/CD)     │
     │    Kakao)      │  │            │  │               │
     └────────────────┘  └────────────┘  └───────────────┘
```

### Travis CI 연동 (구)
![Travis CI](https://user-images.githubusercontent.com/12378642/194440433-f72b1a77-d639-494a-a2fb-4fffffa5ddf2.png)

### Jenkins 연동 (현재)
![Jenkins CI](https://user-images.githubusercontent.com/12378642/202334409-3993e04d-a9bb-4c54-ab59-fe3d0c01d6bf.png)

---

## 🛠️ 기술 스택

### Backend

| 기술 | 버전 | 용도 |
|------|------|------|
| **Java** | 11 | 메인 언어 |
| **Spring Boot** | 2.1.7 | 애플리케이션 프레임워크 |
| **Spring Data JPA** | - | ORM, 데이터 액세스 추상화 |
| **QueryDSL** | 5.0.0 | 타입 안전 동적 쿼리 |
| **Spring Security** | - | 인증/인가 |
| **Spring OAuth2 Client** | - | 소셜 로그인 |
| **Spring Session JDBC** | - | 세션 클러스터링 |

### Database

| 기술 | 용도 |
|------|------|
| **H2** | 개발/테스트 환경 |
| **MariaDB** | 운영 환경 (AWS RDS) |

### Frontend

| 기술 | 용도 |
|------|------|
| **Mustache** | 서버 사이드 템플릿 엔진 |
| **JavaScript/jQuery** | 클라이언트 로직 |
| **CSS** | 스타일링 |

### Infra & DevOps

| 기술 | 용도 |
|------|------|
| **AWS EC2** | 애플리케이션 서버 |
| **AWS RDS** | 데이터베이스 서버 |
| **AWS S3** | 빌드 아티팩트 저장 |
| **AWS CodeDeploy** | 자동 배포 |
| **NGINX** | 리버스 프록시, 로드밸런싱 |
| **Jenkins** | CI/CD 파이프라인 |

### API Documentation

| 기술 | 버전 | 용도 |
|------|------|------|
| **Swagger** | 2.9.2 | API 문서화 |

---

## 🔄 핵심 기능 상세

### 1️⃣ 인증 플로우 (OAuth2)

```
[Client] → 소셜 로그인 요청 → [Spring Security OAuth2]
[OAuth2] → 인증 코드 요청 → [OAuth Provider (Google/Naver/Kakao)]
[Provider] → 인증 코드 반환 → [OAuth2]
[OAuth2] → 액세스 토큰 요청 → [Provider]
[Provider] → 사용자 정보 반환 → [OAuth2]
[OAuth2] → 사용자 저장/업데이트 → [DB]
[OAuth2] → 세션 생성 → [Spring Session]
```

**CustomOAuth2UserService 핵심 로직:**

```java
@Override
public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    // 1. OAuth Provider로부터 사용자 정보 로드
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    
    // 2. Provider 식별 (google, naver, kakao)
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    
    // 3. 사용자 정보 매핑
    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
    
    // 4. 사용자 저장/업데이트
    User user = saveOrUpdate(attributes);
    
    // 5. 세션에 사용자 정보 저장
    httpSession.setAttribute("user", new SessionUser(user));
    
    return new DefaultOAuth2User(...);
}
```

**지원 OAuth Provider:**

| Provider | 특징 |
|----------|------|
| Google | 이메일 기반 식별 |
| Naver | response 객체 내 정보 |
| Kakao | kakao_account 객체 내 정보 |

---

### 2️⃣ 미션 시스템

#### 미션 타입

| 미션 | 기간 | 성공 보상 | 실패 패널티 |
|------|------|----------|------------|
| **Tick 1Day** | 1일 | +10 pt | -10 × 티어배수 |
| **Tick 3Day** | 3일 | +60 pt | -60 × 티어배수 |
| **Quick 5Day** | 5일 | +150 pt | -150 × 티어배수 |
| **Quick 7Day** | 7일 | +280 pt | -280 × 티어배수 |
| **Huick 15Day** | 월간 15일 달성 | +100 pt | - |
| **Huick 30Day** | 월간 전일 달성 | +300 pt | - |

#### 미션 참여 시간

| 시간대 | 참여 가능 시간 |
|--------|---------------|
| **오전 (Afternoon)** | 06:00 ~ 12:30 |
| **오후 (Night)** | 18:00 ~ 24:30 |

#### 미션 생성 플로우

```java
@Transactional
public ApiResult createMission(int missionType, SessionUser sessionUser) {
    // 1. 미션 타입 유효성 검증
    switch (missionType) {
        case 1: case 3: case 5: case 7: break;
        default: return new ApiResult<>(ResponseCode.COMM_E002);
    }
    
    // 2. 사용자 조회
    User user = userRepository.findByUserId(userId).orElse(null);
    
    // 3. 진행중 미션 여부 체크 (중복 참여 방지)
    Mission missionInProgress = missionRepository.findByUserAndProceeding(user, 1).orElse(null);
    if (missionInProgress != null) {
        return new ApiResult<>(ResponseCode.MISS_E000);  // "진행중인 미션이 있습니다."
    }
    
    // 4. 미션 생성
    Mission mission = missionRepository.save(Mission.builder()
            .missionType(missionType)
            .date(localDate.format(DateTimeFormatter.BASIC_ISO_DATE))
            .proceeding(1)  // 진행중
            .successFlag(0)
            .user(user)
            .build());
    
    // 5. 미션 디테일 생성 (일자별)
    for (int i = 0; i < missionType; i++) {
        missionDetailRepository.save(MissionDetail.builder()
                .missionDetailId(new MissionDetailId(mission.getMissionId(), strLocalDate))
                .afternoon(0)
                .night(0)
                .mission(mission)
                .build());
    }
    
    return new ApiResult<>(missionDto);
}
```

#### Took 이벤트 처리

```java
@Transactional
public ResponseCode tookEvent(String date, int time, SessionUser sessionUser) {
    // 1. 참여 가능한 시간 체크
    LocalDateTime localDateTime = LocalDateTime.now(clock);
    LocalDateTime startLimit, endLimit;
    
    if (time == 0) {  // 오전
        startLimit = localDateTime.withHour(6).withMinute(0);
        endLimit = localDateTime.withHour(12).withMinute(30);
    } else {  // 오후
        startLimit = localDateTime.withHour(18).withMinute(0);
        endLimit = localDateTime.plusDays(1).withHour(0).withMinute(30);
    }
    
    if (!(localDateTime.compareTo(startLimit) >= 0 && localDateTime.compareTo(endLimit) <= 0)) {
        return ResponseCode.MISS_E001;  // "참여 가능한 시간이 아닙니다."
    }
    
    // 2. QueryDSL로 미션 참여 여부 업데이트
    JPAUpdateClause jpaUpdateClause = jpaQueryFactory.update(missionDetail);
    
    if (time == 0) {
        jpaUpdateClause.set(missionDetail.afternoon, 1);
    } else {
        jpaUpdateClause.set(missionDetail.night, 1);
    }
    
    // 3. 서브쿼리로 해당 사용자의 진행중 미션 찾아서 업데이트
    long result = jpaUpdateClause.where(missionDetail.missionDetailId.eq(
            JPAExpressions.select(missionDetail.missionDetailId)
                    .from(missionDetail)
                    .join(mission).on(...)
                    .join(user).on(...)
                    .where(...))).execute();
    
    return result > 0 ? ResponseCode.COMM_S000 : ResponseCode.MISS_E002;
}
```

---

### 3️⃣ 티어 시스템

#### 티어 구조

```
┌─────────────────────────────────────────────────────────────────┐
│  MASTER    │ Master 3 (2000~2099) → Master 2 → Master 1 (2200~) │
├─────────────────────────────────────────────────────────────────┤
│  DIAMOND   │ Diamond 4 (1600~) → Diamond 3 → Diamond 2 → Diamond 1 │
├─────────────────────────────────────────────────────────────────┤
│  PLATINUM  │ Platinum 4 (1200~) → Platinum 3 → Platinum 2 → Platinum 1 │
├─────────────────────────────────────────────────────────────────┤
│  GOLD      │ Gold 4 (800~) → Gold 3 → Gold 2 → Gold 1            │
├─────────────────────────────────────────────────────────────────┤
│  SILVER    │ Silver 4 (400~) → Silver 3 → Silver 2 → Silver 1   │
├─────────────────────────────────────────────────────────────────┤
│  BRONZE    │ Bronze 4 (0~99) → Bronze 3 → Bronze 2 → Bronze 1   │
└─────────────────────────────────────────────────────────────────┘
```

#### 티어별 패널티 배수

| 티어 | 패널티 배수 |
|------|------------|
| Bronze | ×1.0 |
| Silver | ×1.5 |
| Gold | ×2.0 |
| Platinum | ×2.5 |
| Diamond | ×3.0 |
| Master | ×3.5 |

**티어 계산 로직:**

```java
public enum TierInfo {
    unranked(-1, 0, 0, 0),
    bronze4(0, 0, 99, 1f),
    bronze3(1, 100, 199, 1f),
    // ... 중략
    master1(22, 2200, 2299, 3.5f);

    private final int tier;
    private final int tierPointStart;
    private final int tierPointEnd;
    private final float penalty;
    
    public static TierInfo findByTierPoint(int tierPoint) {
        // tierPoint에 해당하는 티어 반환
    }
}
```

---

### 4️⃣ 스케줄러 시스템

#### 일일 스케줄 (매일 00:00)

```java
@Scheduled(cron = "0 0 0 * * *")
public void dailySchedule() {
    // 1. 어제 기준으로 종료된 미션 조회
    List<Tuple> tupleList = missionService.selectEndedMission();
    String yesterDay = LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
    
    tupleList.forEach(tuple -> {
        // 2. 미션 성공 여부 판정
        int missionOrder = missionType - Duration.between(endLocalDateTime, currentLocalDateTime).toDays();
        int successFlag = (missionOrder * 2) == tookCount ? 1 : 0;
        
        // 3. 티어 포인트 계산
        int beforeTierRealPoint = tier * 100 + tierPoint;
        int afterTierRealPoint;
        
        if (successFlag == 1) {
            afterTierRealPoint = beforeTierRealPoint + rewardPoint;  // 성공: 보상
        } else {
            rewardPoint = (int) Math.ceil(rewardPoint * beforeTierInfo.getPenalty() * -1);
            afterTierRealPoint = beforeTierRealPoint + rewardPoint;  // 실패: 패널티
        }
        
        // 4. 미션 상태 업데이트
        missionService.updateProceedingAndSuccessFlag(missionId, successFlag);
        
        // 5. 사용자 티어 업데이트
        TierInfo afterTierInfo = TierInfo.findByTierPoint(afterTierRealPoint);
        user.updateTierInfo(afterTierInfo.getTier(), afterTierRealPoint % 100);
        
        // 6. 미션 히스토리 저장
        missionService.saveMissionHistory(...);
    });
}
```

#### 월간 스케줄 (매월 15일 00:30)

```java
@Scheduled(cron = "0 30 0 15 * *")
public void monthSchedule() {
    // 1. 지난달 미션 완수 일수 집계
    List<Tuple> tupleList = missionService.selectLastMission(lastMonthCondition, thisMonthCondition);
    
    tupleList.forEach(tuple -> {
        int successCount = tuple.get(1, Long.class).intValue();
        
        // 2. 월간 미션 보상 지급
        if (successCount >= HALF_DAYS) {
            if (successCount == FULL_DAYS) {
                missionInfo = MissionInfo.Huick_30Day;  // +300pt
            } else {
                missionInfo = MissionInfo.Huick_15Day;  // +100pt
            }
            
            // 티어 업데이트 및 히스토리 저장
            // ...
        }
    });
}
```

---

### 5️⃣ 랭킹 시스템

#### 랭킹 타입

| 타입 | 설명 |
|------|------|
| **전체 랭킹** | 모든 사용자 대상 |
| **미션 랭킹** | 같은 미션 참여자 대상 |

**랭킹 조회 로직:**

```java
public ApiResult selectRankList(int rankType, SessionUser sessionUser) {
    List<User> userList;
    
    // 전체 랭크
    if (rankType == 0) {
        userList = jpaQueryFactory.selectFrom(user)
                .orderBy(user.tier.desc(), user.tierPoint.desc(), user.nickname.asc())
                .fetch();
    // 진행중인 미션 랭크
    } else {
        // 현재 사용자의 미션과 같은 미션 참여자 조회
        userList = jpaQueryFactory.selectFrom(user)
                .join(mission).on(user.userId.eq(mission.user.userId))
                .where(mission.missionType.eq(_mission.getMissionType())
                        .and(mission.date.eq(_mission.getDate())))
                .orderBy(user.tier.desc(), user.tierPoint.desc(), user.nickname.asc())
                .fetch();
    }
    
    // 상위 20명 + 본인 순위 반환
    // ...
}
```

---

## 📊 도메인 모델

### ERD 구조

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────────┐
│    User     │       │     Mission      │       │  MissionDetail  │
├─────────────┤       ├──────────────────┤       ├─────────────────┤
│ email (PK)  │◄──────│ mission_id (PK)  │◄──────│ mission_id (PK) │
│ reg_id (PK) │       │ mission_type     │       │ date (PK)       │
│ name        │       │ date             │       │ afternoon       │
│ nickname    │       │ proceeding       │       │ night           │
│ tier        │       │ success_flag     │       │ created_at      │
│ tier_point  │       │ email (FK)       │       │ modified_at     │
│ role        │       │ reg_id (FK)      │       └─────────────────┘
│ created_at  │       │ created_at       │
│ modified_at │       │ modified_at      │
└─────────────┘       └──────────────────┘
        │
        │              ┌───────────────────┐
        └─────────────►│  MissionHistory   │
                       ├───────────────────┤
                       │ history_id (PK)   │
                       │ mission_type      │
                       │ date              │
                       │ success_flag      │
                       │ change_tier_point │
                       │ took_count        │
                       │ email (FK)        │
                       │ reg_id (FK)       │
                       └───────────────────┘
```

### Entity 상세

#### User Entity

```java
@Entity
public class User extends BaseTimeEntity {
    @EmbeddedId
    private UserId userId;  // 복합키 (email + registrationId)
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String nickname;
    
    @Column(nullable = false)
    private int tier;  // -1: unranked, 0~22: bronze4 ~ master1
    
    @Column(nullable = false)
    private int tierPoint;  // 0~99
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // USER, ADMIN
}
```

#### Mission Entity

```java
@Entity
public class Mission extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MISSION_SEQ_GENERATOR")
    private Long missionId;
    
    @Column(nullable = false)
    private int missionType;  // 1, 3, 5, 7
    
    @Column(nullable = false)
    private String date;  // YYYYMMDD
    
    @Column(nullable = false)
    private int proceeding;  // 0: 완료, 1: 진행중
    
    @Column(nullable = false)
    private int successFlag;  // 0: 실패, 1: 성공
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "email"),
        @JoinColumn(name = "registration_id")
    })
    private User user;
}
```

---

## 📡 API 명세

### Mission API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `GET` | `/api/mission` | 진행중인 미션 조회 |
| `POST` | `/api/mission/{missionType}` | 미션 생성 (1, 3, 5, 7) |
| `PATCH` | `/api/mission/{date}/{time}` | Took 던지기 |
| `GET` | `/api/mission/history` | 미션 히스토리 조회 |

### User API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `GET` | `/api/user` | 사용자 정보 조회 |
| `POST` | `/api/user/nickname` | 닉네임 체크/변경 |
| `GET` | `/api/user/rank/{rankType}` | 랭킹 조회 (0: 전체, 1: 미션) |

### Response 구조

```json
{
    "code": 0,
    "message": "OK",
    "data": { ... }
}
```

### Response Code

| Code | Message | 설명 |
|------|---------|------|
| 0 | OK | 성공 |
| 1000 | Internal Server Error | 서버 에러 |
| 1001 | 처리 중 에러가 발생하였습니다. | 처리 에러 |
| 1002 | 파라미터가 잘못되었습니다. | 파라미터 에러 |
| 2000 | 2자 이상 10자 이하로 입력해주세요. | 닉네임 길이 에러 |
| 2001 | 한글,영문,숫자로 입력해주세요. | 닉네임 형식 에러 |
| 2002 | 중복된 닉네임입니다. | 닉네임 중복 |
| 3000 | 진행중인 미션이 있습니다. | 미션 중복 참여 |
| 3001 | 참여 가능한 시간이 아닙니다. | 미션 시간 외 |
| 3002 | 변경한 이력이 없습니다. | 미션 업데이트 실패 |

---

## 🚀 배포 구조

### Blue-Green 무중단 배포

```
┌────────────────────────────────────────────────────────────────┐
│                           NGINX                                 │
│                    (service-url.inc 참조)                       │
└────────────────────────────────┬───────────────────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    ▼                         ▼
          ┌─────────────────┐       ┌─────────────────┐
          │  real1 (8081)   │       │  real2 (8082)   │
          │    [ACTIVE]     │       │    [IDLE]       │
          └─────────────────┘       └─────────────────┘
```

### 배포 스크립트 플로우 (deploy.sh)

```bash
# 1. Idle 포트 확인 (현재 사용 안 하는 포트)
IDLE_PORT=$(find_idle_port)

# 2. Idle 포트의 기존 프로세스 종료
kill -15 ${IDLE_PID}

# 3. Git Pull & Build
git pull
./gradlew test
./gradlew build

# 4. 새 애플리케이션 실행
nohup java -jar \
    -Dspring.config.location=classpath:/application.properties,... \
    -Dspring.profiles.active=$IDLE_PROFILE \
    $JAR_NAME &

# 5. Health Check
for RETRY_COUNT in {1..10}; do
    RESPONSE=$(curl -s http://localhost:${IDLE_PORT}/profile)
    UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)
    
    if [ ${UP_COUNT} -ge 1 ]; then
        # 6. NGINX 설정 변경 (포트 스위칭)
        echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" \
            | sudo tee /etc/nginx/conf.d/service-url.inc
        
        # 7. NGINX Reload
        sudo service nginx reload
        break
    fi
done
```

### Profile 전략

| Profile | 포트 | 용도 |
|---------|------|------|
| real1 | 8081 | 운영 서버 1 |
| real2 | 8082 | 운영 서버 2 |

---

## 💻 실행 방법

### 로컬 개발 환경

```bash
# 1. 프로젝트 클론
git clone https://github.com/your-repo/ASAP.git
cd ASAP

# 2. OAuth 설정 파일 생성 (application-oauth.properties)
# Google, Naver, Kakao OAuth 클라이언트 정보 설정 필요

# 3. 빌드 및 실행
./gradlew build
./gradlew bootRun

# 4. 접속
# http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "SchedulerFacadeDailySuccessTest"
```

### OAuth 설정 (application-oauth.properties)

```properties
# Google
spring.security.oauth2.client.registration.google.client-id={클라이언트ID}
spring.security.oauth2.client.registration.google.client-secret={시크릿}
spring.security.oauth2.client.registration.google.scope=profile,email

# Naver
spring.security.oauth2.client.registration.naver.client-id={클라이언트ID}
spring.security.oauth2.client.registration.naver.client-secret={시크릿}
spring.security.oauth2.client.registration.naver.redirect-uri={리다이렉트URI}
spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name,email,profile_image
spring.security.oauth2.client.registration.naver.client-name=Naver

# Kakao
spring.security.oauth2.client.registration.kakao.client-id={클라이언트ID}
spring.security.oauth2.client.registration.kakao.client-secret={시크릿}
spring.security.oauth2.client.registration.kakao.redirect-uri={리다이렉트URI}
spring.security.oauth2.client.registration.kakao.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.kakao.scope=profile_nickname,account_email
spring.security.oauth2.client.registration.kakao.client-name=Kakao
spring.security.oauth2.client.registration.kakao.client-authentication-method=POST
```

---

## 🎓 키워드 체크리스트

- [x] OAuth2 (Google, Naver, Kakao)
- [x] Spring Security
- [x] Spring Data JPA
- [x] QueryDSL
- [x] 복합 키 (@EmbeddedId)
- [x] Spring Scheduler (@Scheduled)
- [x] Mustache 템플릿 엔진
- [x] Swagger API 문서화
- [x] Blue-Green 무중단 배포
- [x] NGINX 리버스 프록시
- [x] Jenkins CI/CD
- [x] AWS (EC2, RDS, S3, CodeDeploy)
- [x] 게이미피케이션 (티어/랭킹)
- [x] Session 클러스터링 (Spring Session JDBC)

---

## 📄 라이센스

MIT License
