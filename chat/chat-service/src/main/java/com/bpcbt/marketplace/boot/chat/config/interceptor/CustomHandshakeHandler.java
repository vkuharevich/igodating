package com.bpcbt.marketplace.boot.chat.config.interceptor;

import com.bpcbt.marketplace.boot.user.api.auth.JwtAuthenticationToken;
import com.bpcbt.marketplace.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.bpcbt.marketplace.boot.user.api.global.security.jwt.JwtUserWrapper;
import jakarta.servlet.ServletContext;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.support.AbstractHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * This handshake handler need to set security pricipal to WS session
 * when request outgoing from different backend server (for example "front-backend" services)
 */
public class CustomHandshakeHandler extends AbstractHandshakeHandler implements ServletContextAware {

    public CustomHandshakeHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        RequestUpgradeStrategy strategy = getRequestUpgradeStrategy();
        if (strategy instanceof ServletContextAware servletContextAware) {
            servletContextAware.setServletContext(servletContext);
        }
    }

    /**
     * Override to replace {@code JwtBackendUser} on {@code JwtSecurityUser}.
     * When sending "handshake" query from other backend service {@code JwtBackendAuthenticationFilter} executed (because handshake is http query)
     * and put on context {@code JwtBackendUser}. For convenience it was replaced with {@code JwtSecurityUser}.
     * Thanks to this all requests will be modified using the annotation {@code @SecurityRequestModifier}
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        final Authentication authentication = contextHolderStrategy.getContext().getAuthentication();
        try {
            if (authentication.getPrincipal() instanceof JwtUserWrapper jwtUserWrapper) {
                if (jwtUserWrapper.isBackendWrapper()) {
                    final JwtSecurityUser jwtSecurityUser = jwtUserWrapper.getJwtUser();
                    return new JwtAuthenticationToken(jwtSecurityUser, jwtSecurityUser.getAuthorities());
                }
                return authentication;
            }
            return null;
        } finally {
            contextHolderStrategy.clearContext();
        }
    }
}
