package com.example.chat_application.dto;

import com.example.chat_application.model.CallSignalType;

public class CallSignalDTO {

    private CallSignalType type;

    private Long roomId;

    private Long fromUserId;

    private Long toUserId;   // ✅ ADD THIS

    private Object payload;
    // offer / answer / ice
    private String fromUsername;   // ✅ ADD
    private String toUsername;     // ✅ ADD


    public CallSignalDTO() {}

    public CallSignalDTO(CallSignalType type,
                         Long roomId,
                         Long fromUserId,
                         String fromUsername,
                         Long toUserId,
                         String toUsername,
                         Object payload) {
        this.type = type;
        this.roomId = roomId;
        this.fromUserId = fromUserId;
        this.fromUsername = fromUsername;
        this.toUserId = toUserId;
        this.toUsername = toUsername;
        this.payload = payload;
    }


    public CallSignalType getType() {
        return type;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public Long getToUserId() {          // ✅ REQUIRED
        return toUserId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setType(CallSignalType type) {
        this.type = type;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(Long toUserId) {   // ✅ REQUIRED
        this.toUserId = toUserId;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
