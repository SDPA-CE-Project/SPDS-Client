package com.example.spda_app;

public class UserAccount {
    private String userId;    // Firebase Uid (고유 토큰 정보)
    private String email;    // 이메일 아이디
    private String password;   // 비밀번호 

    public UserAccount() {}
    public UserAccount(String userID, String email, String password) {
        this.userId = userID;
        this.email = email;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() { return email; }
    public void setEmailId(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password;  }

}