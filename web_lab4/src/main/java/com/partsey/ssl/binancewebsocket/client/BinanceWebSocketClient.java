package com.partsey.ssl.binancewebsocket.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class BinanceWebSocketClient extends WebSocketClient {

    public BinanceWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[WebSocket] Connected to Binance stream.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[WebSocket] Message received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.printf("[WebSocket] Connection closed: %s%n", reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[WebSocket] Error: " + ex.getMessage());
    }
}