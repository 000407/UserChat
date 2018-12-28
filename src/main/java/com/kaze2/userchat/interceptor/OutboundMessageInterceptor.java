package com.kaze2.userchat.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MultiValueMap;

public class OutboundMessageInterceptor implements ChannelInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundMessageInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        final StompCommand command = headerAccessor.getCommand();

        LOGGER.info("Outbound channel preSend (" + command + ")...");

        if (command != null) {

            switch (command) {

                case CONNECTED:
                    final StompHeaderAccessor accessor = StompHeaderAccessor.create(headerAccessor.getCommand());
                    accessor.setSessionId(headerAccessor.getSessionId());
                    @SuppressWarnings("unchecked")
                    final MultiValueMap<String, String> nativeHeaders = (MultiValueMap<String, String>) headerAccessor.getHeader(StompHeaderAccessor.NATIVE_HEADERS);
                    accessor.addNativeHeaders(nativeHeaders);

// add custom headers
                    accessor.addNativeHeader("CUSTOM01", "CUSTOM01");

                    return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
                default:
                    break;
            }
        }

        return message;
    }
}
