package com.studyromm.auth.application;

import com.studyromm.platform.security.AccessTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthQueryService authQueryService;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final AuditLogService auditLogService;

    public LoginService(
            AuthQueryService authQueryService,
            PasswordEncoder passwordEncoder,
            AccessTokenService accessTokenService,
            AuditLogService auditLogService
    ) {
        this.authQueryService = authQueryService;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
        this.auditLogService = auditLogService;
    }

    public AuthenticatedLogin authenticate(String username, String password) {
        AuthUser user = authQueryService.findByUsername(username);
        if (user == null || user.passwordHash() == null || !passwordEncoder.matches(password, user.passwordHash())) {
            auditLogService.appendLoginFailure(username);
            throw new BadCredentialsException("invalid username or password");
        }

        auditLogService.appendLoginSuccess(user);
        return new AuthenticatedLogin(
                user,
                accessTokenService.issueToken(user.userId(), user.username(), user.displayName(), user.roleCodes())
        );
    }

    public record AuthenticatedLogin(
            AuthUser user,
            AccessTokenService.IssuedToken issuedToken
    ) {
    }
}
