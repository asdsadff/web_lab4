package com.partsey.ssl.binancewebsocket.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.strutynskyi.ssl.CoinOuterClass;
import com.partsey.ssl.binancewebsocket.client.BinanceWebSocketClient;
import com.partsey.ssl.binancewebsocket.handler.BinanceWebSocketHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BinanceStreamService {
    private final BinanceWebSocketHandler binanceWebSocketHandler;
    private BinanceWebSocketClient client;
    private final List<String> symbols = Arrays.asList("btcusdt", "ethusdt", "adausdt", "solusdt");

    public BinanceStreamService(BinanceWebSocketHandler binanceWebSocketHandler) {
        this.binanceWebSocketHandler = binanceWebSocketHandler;
    }

    @PostConstruct
    public void establishConnection() {
        try {
            String fullUrl = "wss://stream.binance.com:9443/stream?streams=" + buildMultiStreamPath(symbols);
            client = new BinanceWebSocketClient(URI.create(fullUrl)) {
                @Override
                public void onMessage(String message) {
                    try {
                        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
                        String stream = json.get("stream").getAsString();

                        JsonObject data = json.getAsJsonObject("data");
                        if (data == null || !data.has("s") || !data.has("p") || !data.has("T")) {
                            System.err.println("[WebSocket] Missing required fields in message: " + message);
                            return;
                        }

                        String symbol = data.get("s").getAsString();
                        double price = Double.parseDouble(data.get("p").getAsString());
                        long timestamp = data.get("T").getAsLong();

                        CoinOuterClass.Coin coin = CoinOuterClass.Coin.newBuilder()
                                .setStream(stream)
                                .setSymbol(symbol)
                                .setPriceUsdt(price)
                                .setTime(timestamp)
                                .build();

                        binanceWebSocketHandler.sendProtobufToAllSessions(coin.toByteArray());

                    } catch (Exception e) {
                        System.err.println("[WebSocket] Error processing message: " + e.getMessage());
                    }
                }
            };

            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildMultiStreamPath(List<String> symbols) {
        return symbols.stream()
                .map(symbol -> symbol.toLowerCase() + "@trade")
                .collect(Collectors.joining("/"));
    }

    @PreDestroy
    public void closeBinanceConnection() {
        if (client != null && client.isOpen()) {
            client.close();
        }
    }
}

