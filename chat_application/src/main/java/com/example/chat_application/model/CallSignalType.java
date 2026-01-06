package com.example.chat_application.model;

public enum CallSignalType {
    CALL_REQUEST,   // ✅ ADD THIS
    CALL_ACCEPTED,  // ✅ ADD (you already use it)
    CALL_REJECT,
    CALL_END,
    CALL_OFFER,
    ICE_CANDIDATE,
    CALL_ANSWER
}
