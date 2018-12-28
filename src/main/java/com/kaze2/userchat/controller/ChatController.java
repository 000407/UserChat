package com.kaze2.userchat.controller;

import com.kaze2.userchat.model.ChatMessage;
import com.kaze2.userchat.model.OnlineUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private OnlineUsers onlineUsers;

    @MessageMapping("/chat.sendMessage")
    @SendToUser("/topic/message")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        return chatMessage;
        //String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        //messagingTemplate.convertAndSendToUser(sessionId,"/topic/public", new Gson().toJson(chatMessage), headerAccessor.getMessageHeaders());
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        try{
            onlineUsers.addOnlineUser(chatMessage.getSender());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}