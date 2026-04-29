package com.studyromm.knowledge.application;

import com.studyromm.knowledge.api.CurriculumNodeDictionaryItem;
import com.studyromm.knowledge.api.TextbookVersionDictionaryItem;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeDictionaryQueryService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public KnowledgeDictionaryQueryService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TextbookVersionDictionaryItem> listTextbookVersions(String subjectCode, String status) {
        StringBuilder sql = new StringBuilder(
                """
                        select id, code, name, subject_code, status
                        from knowledge_service.textbook_version
                        where is_deleted = false
                        """
        );
        Map<String, Object> params = new java.util.HashMap<>();

        if (subjectCode != null && !subjectCode.isBlank()) {
            sql.append(" and subject_code = :subjectCode");
            params.put("subjectCode", subjectCode);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" and status = :status");
            params.put("status", status);
        }
        sql.append(" order by subject_code asc, code asc");

        return jdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new TextbookVersionDictionaryItem(
                        rs.getString("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("subject_code"),
                        rs.getString("status")
                )
        );
    }

    public List<CurriculumNodeDictionaryItem> listCurriculumNodes(
            String textbookVersionId,
            String subjectCode,
            String nodeType,
            String parentNodeId
    ) {
        StringBuilder sql = new StringBuilder(
                """
                        select n.id, n.textbook_version_id, n.parent_node_id, n.node_code, n.node_name, n.node_type, n.sort_order, n.status
                        from knowledge_service.curriculum_node n
                        join knowledge_service.textbook_version t on t.id = n.textbook_version_id
                        where n.is_deleted = false
                          and t.is_deleted = false
                        """
        );
        Map<String, Object> params = new java.util.HashMap<>();

        if (textbookVersionId != null && !textbookVersionId.isBlank()) {
            sql.append(" and n.textbook_version_id = :textbookVersionId");
            params.put("textbookVersionId", textbookVersionId);
        }
        if (subjectCode != null && !subjectCode.isBlank()) {
            sql.append(" and t.subject_code = :subjectCode");
            params.put("subjectCode", subjectCode);
        }
        if (nodeType != null && !nodeType.isBlank()) {
            sql.append(" and n.node_type = :nodeType");
            params.put("nodeType", nodeType);
        }
        if (parentNodeId == null) {
            sql.append(" and n.parent_node_id is null");
        } else if (!parentNodeId.isBlank()) {
            sql.append(" and n.parent_node_id = :parentNodeId");
            params.put("parentNodeId", parentNodeId);
        }

        sql.append(" order by n.sort_order asc, n.node_code asc");

        return jdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> new CurriculumNodeDictionaryItem(
                        rs.getString("id"),
                        rs.getString("textbook_version_id"),
                        rs.getString("parent_node_id"),
                        rs.getString("node_code"),
                        rs.getString("node_name"),
                        rs.getString("node_type"),
                        rs.getInt("sort_order"),
                        rs.getString("status")
                )
        );
    }
}
