package com.example.chat_application.websocket;

import com.example.chat_application.dto.CallSignalDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CallSignalingController {

    private final SimpMessagingTemplate messagingTemplate;

    public CallSignalingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/call.signal")
    public void signal(CallSignalDTO signal) {

        System.out.println(
                "📞 Sending CALL_REQUEST to user: " + signal.getToUsername()
        );

        messagingTemplate.convertAndSendToUser(
                signal.getToUsername(),   // MUST match Principal name
                "/queue/call",
                signal
        );
    }





}

