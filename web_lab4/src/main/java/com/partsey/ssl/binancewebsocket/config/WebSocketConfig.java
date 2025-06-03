package com.partsey.ssl.binancewebsocket.config;

import com.partsey.ssl.binancewebsocket.handler.BinanceWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final BinanceWebSocketHandler binanceWebSocketHandler;

    @Autowired
    public WebSocketConfig(BinanceWebSocketHandler binanceWebSocketHandler) {
        this.binanceWebSocketHandler = binanceWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(binanceWebSocketHandler, "/ws/binance")
                .setAllowedOrigins("*");
    }
}
