
package com.example.spda_app;

public class UserAccount {
    private String userId;    // Firebase Uid (고유 토큰 정보)
    private String email;    // 이메일 아이디
    private String password;   // 비밀번호
    private String testdata;

    private String Username;

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    private int levelIndex;

    public int getVibrationIndex() {
        return vibrationIndex;
    }

    public void setVibrationIndex(int vibrationIndex) {
        this.vibrationIndex = vibrationIndex;
    }

    private int vibrationIndex;

    public int getAlarmBellIndex() {
        return alarmBellIndex;
    }

    public void setAlarmBellIndex(int alarmBellIndex) {
        this.alarmBellIndex = alarmBellIndex;
    }

    private int alarmBellIndex;

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    private int songIndex;


    public UserAccount() {}
    public UserAccount(String userID, String email, String password, String name) {
        this.userId = userID;
        this.email = email;
        this.password = password;
        this.Username = name;

        levelIndex = 0;
        vibrationIndex= 0;
        alarmBellIndex= 0;
        songIndex= 0;
    }
    public UserAccount(String userID, String email, String password) {
        this.userId = userID;
        this.email = email;
        this.password = password;

        levelIndex = 0;
        vibrationIndex= 0;
        alarmBellIndex= 0;
        songIndex= 0;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public void setUserName(String name) {this.Username = name;}
    public String getUserName() {return this.Username;}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password;  }

    public String getTestdata() { return testdata; }
    public void setTestdata(String testdata) { this.testdata = testdata;  }

}
