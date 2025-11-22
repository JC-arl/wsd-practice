package kr.ac.jbnu.jc.wsdpractice02.controller;

import kr.ac.jbnu.jc.wsdpractice02.common.ApiResponse;
import kr.ac.jbnu.jc.wsdpractice02.model.domain.User;
import kr.ac.jbnu.jc.wsdpractice02.model.dto.UserDto;
import kr.ac.jbnu.jc.wsdpractice02.model.request.CreateUserRequest;
import kr.ac.jbnu.jc.wsdpractice02.model.request.LoginRequest;
import kr.ac.jbnu.jc.wsdpractice02.model.request.UpdateUserRequest;
import kr.ac.jbnu.jc.wsdpractice02.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // GET 1: 모든 유저 조회
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.findAll().stream()
                .map(UserDto::from)
                .collect(Collectors.toList());

        return ResponseEntity //(200 OK)
                .ok(ApiResponse.success(users, "전체 사용자 조회 성공"));
    }

    // GET 2: 특정 유저 조회
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean simulateError) {

        if (simulateError) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE) //503 Service Unavailable
                    .body(ApiResponse.error("서버 점검 중입니다. (simulateError=true)"));
        }

        return userService.findById(id)
                .map(user -> ResponseEntity.ok( //200 OK
                        ApiResponse.success(UserDto.from(user), "사용자 조회 성공")))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND) //404 Not Found
                        .body(ApiResponse.error("사용자를 찾을 수 없습니다.")));
    }

    // POST 1: 사용자 생성
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDto>> createUser(
            @RequestBody CreateUserRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) //400 Bad Request
                    .body(ApiResponse.error("username, password는 필수입니다."));
        }

        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) //400 Bad Request
                    .body(ApiResponse.error("이미 존재하는 username입니다."));
        }

        User user = userService.createUser(request.getUsername(), request.getPassword());
        UserDto userDto = UserDto.from(user);

        return ResponseEntity
                .status(HttpStatus.CREATED) //201 Created
                .body(ApiResponse.success(userDto, "사용자 생성 성공"));
    }

    // POST 2: 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestBody LoginRequest request) {

        return userService.findByUsername(request.getUsername())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .map(user -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", user.getId());
                    data.put("username", user.getUsername());

                    return ResponseEntity //200 OK
                            .ok(ApiResponse.success(data, "로그인 성공"));
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                        .body(ApiResponse.error("아이디 또는 비밀번호가 올바르지 않습니다.")));
    }

    // PUT 1: 사용자 이름 수정
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUsername(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) //400 Bad Request
                    .body(ApiResponse.error("새로운 username은 비어 있을 수 없습니다."));
        }

        return userService.updateUsername(id, request.getUsername())
                .map(user -> ResponseEntity.ok( //200 OK
                        ApiResponse.success(UserDto.from(user), "사용자 이름 수정 성공")))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND) //404 Not Found
                        .body(ApiResponse.error("사용자를 찾을 수 없습니다.")));
    }

    // PUT 2: 비밀번호 변경
    @PutMapping("/users/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody LoginRequest request) {

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
                    .body(ApiResponse.error("새로운 비밀번호는 비어 있을 수 없습니다."));
        }

        boolean changed = userService.changePassword(id, request.getPassword());
        if (!changed) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) //404 Not Found
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
        }

        return ResponseEntity   //200 OK
                .ok(ApiResponse.success(null, "비밀번호 변경 성공"));
    }

    // DELETE 1: 특정 사용자 삭제
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteById(id);
        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)// 404 Not Found
                    .body(ApiResponse.error("삭제할 사용자를 찾을 수 없습니다."));
        }

        return ResponseEntity //200 OK
                .ok(ApiResponse.success(null, "사용자 삭제 성공"));
    }

    // DELETE 2: 전체 사용자 삭제
    @DeleteMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteAllUsers(
            @RequestParam(required = false, defaultValue = "false") boolean simulateError) {

        if (simulateError) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) //500 Internal Server Error
                    .body(ApiResponse.error("서버 내부 에러가 발생한 것처럼 시뮬레이션합니다."));
        }

        int deletedCount = userService.deleteAll();
        Map<String, Object> data = new HashMap<>();
        data.put("deletedCount", deletedCount);

        return ResponseEntity   //200 OK
                .ok(ApiResponse.success(data, "전체 사용자 삭제 완료"));
    }
}
