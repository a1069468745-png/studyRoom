package com.studyromm.platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class StudyRoomAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    public StudyRoomAuthenticationFilter(
            AccessTokenService accessTokenService,
            ApiAuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.accessTokenService = accessTokenService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthenticatedActor actor = resolveActor(request);
                if (actor != null) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new AuthenticatedActorAuthenticationToken(actor, toAuthorities(actor))
                    );
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, exception);
        }
    }

    private AuthenticatedActor resolveActor(HttpServletRequest request) {
        String authorization = request.getHeader(SecurityHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(SecurityHeaders.BEARER_PREFIX)) {
            String token = authorization.substring(SecurityHeaders.BEARER_PREFIX.length()).trim();
            if (!token.isEmpty()) {
                return accessTokenService.parseUserToken(token);
            }
        }

        String serviceName = request.getHeader(SecurityHeaders.SERVICE_NAME);
        String serviceToken = request.getHeader(SecurityHeaders.SERVICE_TOKEN);
        if (serviceName != null && !serviceName.isBlank() && serviceToken != null && !serviceToken.isBlank()) {
            return accessTokenService.parseServiceActor(serviceName, serviceToken);
        }
        return null;
    }

    private List<SimpleGrantedAuthority> toAuthorities(AuthenticatedActor actor) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String roleCode : actor.roleCodes()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleCode));
        }
        if (actor.actorType() == AuthenticatedActor.ActorType.SERVICE) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SERVICE"));
        }
        return authorities;
    }
}
