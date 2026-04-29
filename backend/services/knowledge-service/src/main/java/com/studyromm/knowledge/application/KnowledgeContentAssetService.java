package com.studyromm.knowledge.application;

import com.studyromm.knowledge.api.ContentAssetItem;
import com.studyromm.knowledge.api.CreateContentAssetRequest;
import com.studyromm.knowledge.api.CreateContentAssetResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KnowledgeContentAssetService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public KnowledgeContentAssetService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ContentAssetItem> listAdminAssets(String curriculumNodeId, String reviewStatus, String publishStatus) {
        StringBuilder sql = new StringBuilder(
                """
                        select id, curriculum_node_id, asset_type, title, body_markdown, review_status, publish_status
                        from knowledge_service.content_asset
                        where is_deleted = false
                        """
        );
        Map<String, Object> params = new java.util.HashMap<>();
        if (curriculumNodeId != null && !curriculumNodeId.isBlank()) {
            sql.append(" and curriculum_node_id = :curriculumNodeId");
            params.put("curriculumNodeId", curriculumNodeId);
        }
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            sql.append(" and review_status = :reviewStatus");
            params.put("reviewStatus", reviewStatus);
        }
        if (publishStatus != null && !publishStatus.isBlank()) {
            sql.append(" and publish_status = :publishStatus");
            params.put("publishStatus", publishStatus);
        }
        sql.append(" order by created_at desc, id desc");
        return jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> new ContentAssetItem(
                rs.getString("id"),
                rs.getString("curriculum_node_id"),
                rs.getString("asset_type"),
                rs.getString("title"),
                rs.getString("body_markdown"),
                rs.getString("review_status"),
                rs.getString("publish_status")
        ));
    }

    public List<ContentAssetItem> listClientPublishedAssets(String curriculumNodeId) {
        return listAdminAssets(curriculumNodeId, "APPROVED", "PUBLISHED");
    }

    @Transactional
    public CreateContentAssetResponse createDraftAsset(CreateContentAssetRequest request) {
        if (request == null
                || isBlank(request.curriculumNodeId())
                || isBlank(request.assetType())
                || isBlank(request.title())) {
            throw new IllegalArgumentException("curriculumNodeId, assetType and title are required");
        }

        String assetId = "asset-" + UUID.randomUUID();
        jdbcTemplate.update(
                """
                        insert into knowledge_service.content_asset
                        (id, curriculum_node_id, asset_type, title, body_markdown, review_status, publish_status)
                        values (:id, :curriculumNodeId, :assetType, :title, :bodyMarkdown, :reviewStatus, :publishStatus)
                        """,
                Map.of(
                        "id", assetId,
                        "curriculumNodeId", request.curriculumNodeId(),
                        "assetType", request.assetType(),
                        "title", request.title(),
                        "bodyMarkdown", request.bodyMarkdown(),
                        "reviewStatus", "DRAFT",
                        "publishStatus", "UNPUBLISHED"
                )
        );
        return new CreateContentAssetResponse(assetId, "DRAFT", "UNPUBLISHED");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
