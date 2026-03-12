package com.fpt.glasseshop.entity;

import lombok.AllArgsConstructor;
public class AuthData {
    private Long userId;
    private boolean status;

    public AuthData() {
    }
    public AuthData(Long userId, boolean status) {
        this.userId = userId;
        this.status = status;
    }
    public Long getUserId() {
        return userId;
    }
    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
