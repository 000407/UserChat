package com.kaze2.userchat.model;

import com.kaze2.userchat.exception.DuplicateUserException;
import com.kaze2.userchat.util.MD5;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OnlineUsers {

    private Map<String, String> onlineUsers = new HashMap<>();

    public void addOnlineUser(String name) throws NoSuchAlgorithmException, DuplicateUserException {
        if(this.onlineUsers.keySet().contains(name))
            throw new DuplicateUserException("Username already exists");

        this.onlineUsers.put(MD5.digest(name), name);
    }

    public Map<String, String> getOnlineUsers() {
        return onlineUsers;
    }
}
