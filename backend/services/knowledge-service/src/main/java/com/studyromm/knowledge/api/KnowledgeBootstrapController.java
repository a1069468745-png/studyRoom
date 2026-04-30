package com.studyromm.knowledge.api;

import com.studyromm.knowledge.application.KnowledgeDictionaryQueryService;
import com.studyromm.knowledge.application.KnowledgeContentAssetService;
import com.studyromm.knowledge.application.QuestionBankService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KnowledgeBootstrapController {

    private final KnowledgeDictionaryQueryService dictionaryQueryService;
    private final KnowledgeContentAssetService contentAssetService;
    private final QuestionBankService questionBankService;

    public KnowledgeBootstrapController(
            KnowledgeDictionaryQueryService dictionaryQueryService,
            KnowledgeContentAssetService contentAssetService,
            QuestionBankService questionBankService
    ) {
        this.dictionaryQueryService = dictionaryQueryService;
        this.contentAssetService = contentAssetService;
        this.questionBankService = questionBankService;
    }

    @GetMapping("/api/knowledge/bootstrap")
    public Map<String, Object> bootstrap() {
        return Map.of(
                "service", "knowledge-service",
                "status", "BOOTSTRAP_READY",
                "scopes", List.of("curriculum-tree", "knowledge-points", "content-assets")
        );
    }

    @GetMapping("/api/common/dictionaries")
    public Map<String, Object> dictionaries() {
        List<TextbookVersionDictionaryItem> textbookVersions = dictionaryQueryService.listTextbookVersions(null, "ACTIVE");
        return Map.of(
                "textbookVersions", textbookVersions,
                "curriculumNodeTypes", List.of("STAGE", "GRADE", "SUBJECT", "UNIT", "CHAPTER", "KNOWLEDGE")
        );
    }

    @GetMapping("/api/common/dictionaries/textbook-versions")
    public List<TextbookVersionDictionaryItem> textbookVersions(
            @RequestParam(required = false) String subjectCode,
            @RequestParam(required = false) String status
    ) {
        return dictionaryQueryService.listTextbookVersions(subjectCode, status);
    }

    @GetMapping("/api/common/dictionaries/curriculum-nodes")
    public List<CurriculumNodeDictionaryItem> curriculumNodes(
            @RequestParam(required = false) String textbookVersionId,
            @RequestParam(required = false) String subjectCode,
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String parentNodeId
    ) {
        return dictionaryQueryService.listCurriculumNodes(textbookVersionId, subjectCode, nodeType, parentNodeId);
    }

    @GetMapping("/api/admin/knowledge/content-assets")
    public List<ContentAssetItem> adminContentAssets(
            @RequestParam(required = false) String curriculumNodeId,
            @RequestParam(required = false) String reviewStatus,
            @RequestParam(required = false) String publishStatus
    ) {
        return contentAssetService.listAdminAssets(curriculumNodeId, reviewStatus, publishStatus);
    }

    @GetMapping("/api/client/knowledge/content-assets")
    public List<ContentAssetItem> clientContentAssets(
            @RequestParam(required = false) String curriculumNodeId
    ) {
        return contentAssetService.listClientPublishedAssets(curriculumNodeId);
    }

    @PostMapping("/api/admin/knowledge/content-assets")
    public CreateContentAssetResponse createContentAsset(@RequestBody CreateContentAssetRequest request) {
        return contentAssetService.createDraftAsset(request);
    }

    @GetMapping("/api/admin/questions")
    public List<QuestionItem> adminQuestions(
            @RequestParam(required = false) String subjectCode,
            @RequestParam(required = false) String reviewStatus
    ) {
        return questionBankService.listAdminQuestions(subjectCode, reviewStatus);
    }

    @PostMapping("/api/admin/questions")
    public CreateQuestionResponse createQuestion(@RequestBody CreateQuestionRequest request) {
        return questionBankService.createQuestion(request);
    }

    @GetMapping("/api/client/questions")
    public List<ClientQuestionItem> clientQuestions(
            @RequestParam(required = false) String subjectCode,
            @RequestParam(required = false) String gradeCode,
            @RequestParam(required = false) String questionType
    ) {
        return questionBankService.listClientQuestions(subjectCode, gradeCode, questionType);
    }

    @PostMapping("/api/admin/questions/{questionId}/review")
    public ReviewQuestionResponse reviewQuestion(
            @PathVariable String questionId,
            @RequestBody ReviewQuestionRequest request
    ) {
        return questionBankService.reviewQuestion(questionId, request);
    }
}
