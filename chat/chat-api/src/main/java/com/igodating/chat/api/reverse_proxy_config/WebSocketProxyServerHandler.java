package com.igodating.chat.api.reverse_proxy_config;

import com.igodating.boot.user.api.global.security.jwt.JwtSecurityUser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.crypto.SecretKey;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class WebSocketProxyServerHandler extends AbstractWebSocketHandler {
    private final Map<String, ServerProxy> sessionId2server = new ConcurrentHashMap<>();
    private final WebSocketClient webSocketClient;
    private final Function<Principal, JwtSecurityUser> funcGetJwtTokenByPrincipal;
    private final String urlToConnect;
    private final SecretKey secretKey;

    public WebSocketProxyServerHandler(WebSocketClient webSocketClient,
                                       Function<Principal, JwtSecurityUser> funcGetJwtTokenByPrincipal,
                                       String urlToConnect,
                                       SecretKey secretKey) {
        this.webSocketClient = webSocketClient;
        this.funcGetJwtTokenByPrincipal = funcGetJwtTokenByPrincipal;
        this.urlToConnect = urlToConnect;
        this.secretKey = secretKey;
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        getProxyServer(webSocketSession).sendMessageToNextServer(webSocketMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
        super.afterConnectionClosed(webSocketSession, status);
        ServerProxy serverProxy = sessionId2server.get(webSocketSession.getId());
        if (serverProxy != null) {
            serverProxy.close();
        }
    }

    private ServerProxy getProxyServer(WebSocketSession webSocketSession) {
        ServerProxy serverProxy = sessionId2server.get(webSocketSession.getId());
        if (serverProxy == null) {
            serverProxy = new ServerProxy(webSocketSession, () -> this.sessionId2server.remove(webSocketSession.getId()),
                    this.webSocketClient, urlToConnect, funcGetJwtTokenByPrincipal, secretKey);
            sessionId2server.put(webSocketSession.getId(), serverProxy);
        }
        return serverProxy;
    }


}
