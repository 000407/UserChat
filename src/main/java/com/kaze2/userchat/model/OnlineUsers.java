package com.kaze2.userchat.model;

import com.kaze2.userchat.util.MD5;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OnlineUsers {

    private Map<String, String> onlineUsers = new HashMap<>();

    public void addOnlineUser(String name) throws NoSuchAlgorithmException {
        String digest = MD5.digest(name);
        if(this.onlineUsers.keySet().contains(digest))
            return;

        this.onlineUsers.put(digest, name);
    }

    public Map<String, String> getOnlineUsers() {
        return onlineUsers;
    }

    public Object[] getOnlineUsernames(){
        return this.onlineUsers.values().toArray();
    }

    public boolean isOnline(String name){
        return this.onlineUsers.containsValue(name);
    }
}
