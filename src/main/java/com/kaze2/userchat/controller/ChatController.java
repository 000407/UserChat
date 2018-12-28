package com.kaze2.userchat.controller;

import com.google.gson.Gson;
import com.kaze2.userchat.exception.InvalidRecipientException;
import com.kaze2.userchat.model.ChatMessage;
import com.kaze2.userchat.model.OnlineUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Collection;

@Controller
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private OnlineUsers onlineUsers;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) throws InvalidRecipientException {
        //TODO: Check if Spring Security blocks access to these URLs
        //TODO:(OPTIONAL) Sender could be authenticated using the principal
        String recipient = chatMessage.getRecipient();

        if(!onlineUsers.isOnline(recipient))
            throw new InvalidRecipientException("Recipient was not found in the online list");

        messagingTemplate.convertAndSendToUser(chatMessage.getRecipient(), "/queue/new", chatMessage);
    }

    @MessageMapping("/users/online")
    public void getUsers(@Payload ChatMessage chatMessage,
                         SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String onlineUsernames = new Gson().toJson(onlineUsers.getOnlineUsernames());
        chatMessage.setType(ChatMessage.MessageType.SYSTEM);
        chatMessage.setSender("system");
        chatMessage.setContent(onlineUsernames);

        messagingTemplate.convertAndSendToUser(principal.getName(), "/users/online", chatMessage);
    }
}