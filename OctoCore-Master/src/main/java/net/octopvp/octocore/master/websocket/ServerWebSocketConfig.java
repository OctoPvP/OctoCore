package net.octopvp.aetheriacoremaster.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class ServerWebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/api/ws", "/ws").setAllowedOriginPatterns("*").setAllowedOrigins("localhost", "127.0.0.1", "localhost:3000", "127.0.0.1:3000");
    }
    @Bean
    public WebSocketHandler webSocketHandler() {
        return new ServerWebSocketHandler();
    }

}
