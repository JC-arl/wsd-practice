package kr.ac.jbnu.jc.wsdpractice02.model.request;

public class UpdateUserRequest {

    private String username;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String username) {
        this.username = username;
    }

    // getter / setter
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
}
