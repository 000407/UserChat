package com.kaze2.userchat.config;


import com.kaze2.userchat.interceptor.HttpHandshakeInterceptor;
import com.kaze2.userchat.interceptor.OutboundMessageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost", "http://127.0.0.1")
                .addInterceptors(new HttpHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/userchat");
	    registry.enableSimpleBroker("/topic", "/queue", "/users");
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        //registration.interceptors(new OutboundMessageInterceptor());
    }
}
