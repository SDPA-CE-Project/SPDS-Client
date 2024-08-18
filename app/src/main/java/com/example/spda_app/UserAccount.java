package com.example.spda_app;

public class UserAccount {

    private static UserAccount instance;
    public static UserAccount GetInstance()
    {
        if(instance == null)
            instance = new UserAccount();
        return instance;
    }

    private String userId;    // Firebase Uid (고유 토큰 정보)
    private String email;    // 이메일 아이디
    private String password;   // 비밀번호 

    public UserAccount() {}


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