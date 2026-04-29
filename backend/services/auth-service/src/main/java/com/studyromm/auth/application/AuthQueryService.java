package com.studyromm.auth.application;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthQueryService {

    private final JdbcTemplate jdbcTemplate;

    public AuthQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AuthUser findByUsername(String username) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                        select u.id as user_id,
                               u.username,
                               u.display_name,
                               u.password_hash,
                               r.code as role_code
                        from auth_service.`user` u
                        left join auth_service.user_role ur on ur.user_id = u.id and ur.is_deleted = false
                        left join auth_service.`role` r on r.id = ur.role_id and r.is_deleted = false
                        where u.username = ?
                          and u.is_deleted = false
                          and u.status = 'ACTIVE'
                        order by r.code
                        """,
                username
        );
        if (rows.isEmpty()) {
            return null;
        }

        Map<String, Object> first = rows.get(0);
        Map<String, String> roles = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object roleCode = row.get("role_code");
            if (roleCode != null) {
                roles.put(roleCode.toString(), roleCode.toString());
            }
        }

        return new AuthUser(
                first.get("user_id").toString(),
                first.get("username").toString(),
                first.get("display_name").toString(),
                first.get("password_hash") == null ? null : first.get("password_hash").toString(),
                List.copyOf(roles.values())
        );
    }
}
