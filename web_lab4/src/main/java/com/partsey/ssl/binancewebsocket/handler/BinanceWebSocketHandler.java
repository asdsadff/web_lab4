package com.partsey.ssl.binancewebsocket.handler;

import com.partsey.ssl.jwt.JwtDecoder;
import com.partsey.ssl.openidconnect.OpenIdConnectProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BinanceWebSocketHandler extends TextWebSocketHandler {
    private final JwtDecoder jwtDecoder;
    private final Set<WebSocketSession> sessions = new HashSet<>();

    public BinanceWebSocketHandler(OpenIdConnectProperties openIdConnectProperties) {
        this.jwtDecoder = new JwtDecoder(openIdConnectProperties);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(isTokenValid(session)) {
            sessions.add(session);
            System.out.println("Websocket client connected");
        }
        else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    private String getTokenFromCookies(WebSocketSession session) {
        List<String> cookieHeaders = session.getHandshakeHeaders().get("cookie");
        if (cookieHeaders != null) {
            for (String header : cookieHeaders) {
                String[] cookies = header.split(";");
                for (String cookie : cookies) {
                    String[] parts = cookie.trim().split("=");
                    if (parts.length == 2) {
                        String name = parts[0];
                        String value = parts[1];
                        if ("access_token".equals(name)) {
                            return value;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isTokenValid(WebSocketSession session) {
        String token = getTokenFromCookies(session);
        if (token == null) {
            return false;
        }
        try {
            jwtDecoder.decodeToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void sendProtobufToAllSessions(byte[] protobufMessage) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new BinaryMessage(protobufMessage));
                }
            } catch (IOException e) {
                System.err.println("Error sending binary protobuf message: " + e.getMessage());
            }
        });
    }
}
