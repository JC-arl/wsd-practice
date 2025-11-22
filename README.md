# wsd-practice

spring boot기반 MVC 패턴으로
8개의 login rest api 구현함.

## 📦 프로젝트 구조 (MVC + Model 분리)

```text 
src/main/java/kr/ac/jbnu/jc/wsdpractice02
 ├ common
 │   └ ApiResponse.java            # 표준화된 응답 포맷
 ├ config
 │   └ LoggingFilter.java          # 요청/응답 로깅 미들웨어
 ├ controller
 │   └ UserController.java         # Controller (총 8개 REST API)
 ├ service
 │   └ UserService.java            # 비즈니스 로직 처리 (Service Layer)
 └ model
     ├ domain
     │   └ User.java               # 사용자 도메인 객체
     ├ dto
     │   └ UserDto.java            # 응답 DTO
     └ request
         ├ CreateUserRequest.java  # 사용자 생성 요청 DTO
         ├ UpdateUserRequest.java  # 사용자 수정 요청 DTO
         └ LoginRequest.java       # 로그인 요청 DTO
```

### API 요구사항 – 총 8개의 HTTP 메서드 구현

각 HTTP 메서드(GET, POST, PUT, DELETE)별로 2개씩, 총 8개의 API를 제공합니다.

- GET	
  - /api/users	모든 사용자 조회
  - /api/users/{id}	특정 사용자 조회
- POST	
  - /api/users	사용자 생성
  - /api/login	로그인
- PUT	
  - /api/users/{id}	사용자 이름 수정
  - /api/users/{id}/password	사용자 비밀번호 변경
- DELETE	
  - /api/users/{id}	특정 사용자 삭제
  - /api/users	전체 사용자 삭제 
- 추가 status
  - 에러 시뮬레이션 → 503 Service Unavailable
  - /api/users/1?simulateError=true
  - /api/users?simulateError=true
### 응답 포맷 표준화 (ApiResponse)
- 모든 컨트롤러는 아래 구조를 기반으로 응답을 반환
```
성공 응답 예시
{
"status": "success",
"data": { ... },
"message": "요청 성공 메시지"
}

에러 응답 예시
{
"status": "error",
"data": null,
"message": "에러 메시지"
}
```

### 미들웨어(Middleware) – LoggingFilter

- LoggingFilter는 모든 요청에 대해 아래 내용을 로그로 출력
  - HTTP Method (GET, POST, PUT, DELETE)
  - 요청 URL
  - 응답 Status Code
  - 처리 시간(ms)
```
로그 출력 예시
[REQUEST] GET /api/users
[RESPONSE] GET /api/users -> status: 200 (12 ms)
```