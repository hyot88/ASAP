> # ASAP
> ### 42D Project v1 - ASAP 서버
> 운영 URL: [ASAP](http://ec2-52-78-162-180.ap-northeast-2.compute.amazonaws.com)

### 1. ASAP 시스템 구조
![image](https://user-images.githubusercontent.com/12378642/194440433-f72b1a77-d639-494a-a2fb-4fffffa5ddf2.png)

### 2. 사용 기술
  * Spring Boot 2.1.7
  * JAVA 11
  * JPA
  * OAuth2
  * Swagger 2.9.2
  * mustache
  * jquery, javascript
  * h2 database
  * AWS (EC2, RDS, IAM, S3, CodeDeploy)
  * Travis CI
  * NGINX 1.20

### 3. 현재까지 구현된 기능
* **로그인 화면**
    * OAuth2 로그인 (google, naver, kakao)
* **닉네임 체크 화면**
    * 닉네임을 입력받아서 유효성 체크를 진행 후 저장
* **메인 화면**
    * 로그인 후 메인 화면
