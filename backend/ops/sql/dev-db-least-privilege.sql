-- DEV-004 P0 安全收口：本地开发库最小权限账号
-- 执行前请替换密码占位符，并在执行后通过 SHOW GRANTS 复核。

CREATE USER IF NOT EXISTS 'studyromm_app'@'localhost' IDENTIFIED BY 'REPLACE_WITH_STRONG_PASSWORD';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON auth_service.* TO 'studyromm_app'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON knowledge_service.* TO 'studyromm_app'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON question_service.* TO 'studyromm_app'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON exam_service.* TO 'studyromm_app'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON job_service.* TO 'studyromm_app'@'localhost';

FLUSH PRIVILEGES;
