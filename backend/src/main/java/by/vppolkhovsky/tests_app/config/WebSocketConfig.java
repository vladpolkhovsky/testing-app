package by.vppolkhovsky.tests_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024); // 128KB
        registration.setSendBufferSizeLimit(512 * 1024); // 512KB
        registration.setSendTimeLimit(20 * 1000); // 20 seconds
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint для подключения WebSocket с поддержкой SockJS
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*") // В production укажите конкретные домены
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Префикс для отправки сообщений клиентами (MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");

        // Префикс для подписок (SimpleBroker)
        registry.enableSimpleBroker("/topic", "/queue", "/user");

        // Префикс для user-specific сообщений
        registry.setUserDestinationPrefix("/user");
    }
}