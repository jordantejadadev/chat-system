package com.jordan.chat_system.security.websocket;

import com.jordan.chat_system.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.messaging.support.MessageBuilder;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

//    @Override
//    public Message<?> preSend(
//            Message<?> message,
//            MessageChannel channel
//    ) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (accessor.getCommand() == StompCommand.CONNECT) {
//            // Aquí autenticamos el usuario
//            String authHeader = accessor.getFirstNativeHeader("Authorization");
//            System.out.println(authHeader);
//        }
//        return message;
//    }


//@Override
//public Message<?> preSend(
//        Message<?> message,
//        MessageChannel channel
//) {
//    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//    System.out.println("Comando: " + accessor.getCommand());
//    System.out.println("SessionId: " + accessor.getSessionId());
//
//    if (accessor.getCommand() == StompCommand.SEND) {
//        System.out.println("SEND User: " + accessor.getUser());
//        System.out.println("SEND Session: " + accessor.getSessionId());
//    }
//
//    if (accessor.getCommand() == StompCommand.CONNECT) {
//
//        System.out.println("CONNECT detectado");
//
//        String authHeader =
//                accessor.getFirstNativeHeader("Authorization");
//
//        System.out.println("Header: " + authHeader);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return message;
//        }
//
//        String jwt = authHeader.substring(7);
//
//        System.out.println("1");
//        String username = jwtService.extractUsername(jwt);
//        System.out.println("2");
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        System.out.println("3");
//        boolean valid = jwtService.isTokenValid(jwt, userDetails);
//        System.out.println("4" + valid);
//
//        if (valid) {
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    userDetails,
//                    null,
//                    userDetails.getAuthorities()
//            );
//            System.out.println("5");
//            accessor.setUser(authentication);
//            System.out.println("6");
//        }
//
//    }
//
//    return message;
//}

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                String username = jwtService.extractUsername(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    accessor.setUser(authentication);
                }
            }
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
