package kr.ac.jbnu.jc.wsdpractice02.model.dto;

import kr.ac.jbnu.jc.wsdpractice02.model.domain.User;

public class UserDto {

    private Long id;
    private String username;

    public UserDto() {}

    public UserDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }

    // getter / setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {this.username = username; }
}
