package com.example.spda_app;

public class UserStatus {
    public String key;
    public boolean chkConnectRequest;
    public boolean chkConnectResponse;

    public UserStatus(String key, boolean chkConnectRequest, boolean chkConnectResponse) {
        this.key = key;
        this.chkConnectRequest = chkConnectRequest;
        this.chkConnectResponse = chkConnectResponse;
    }
    public UserStatus(String key, boolean chkConnectRequest) {
        this.key = key;
        this.chkConnectRequest = chkConnectRequest;
    }
}
