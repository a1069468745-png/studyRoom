import type { ClientQuestionItem, ContentAssetItem, CurriculumNodeDictionaryItem } from "@study-room/shared";

export const learningFixtures: {
  nodes: CurriculumNodeDictionaryItem[];
  assets: ContentAssetItem[];
  questions: ClientQuestionItem[];
} = {
  nodes: [
    {
      nodeId: "node-unit-1",
      textbookVersionId: "tb-rj-g9-math",
      textbookVersionName: "人教版九年级数学",
      subjectCode: "MATH",
      parentNodeId: null,
      nodeCode: "UNIT-01",
      nodeName: "第一单元",
      nodeType: "UNIT",
      sortOrder: 1
    },
    {
      nodeId: "node-chapter-1",
      textbookVersionId: "tb-rj-g9-math",
      textbookVersionName: "人教版九年级数学",
      subjectCode: "MATH",
      parentNodeId: "node-unit-1",
      nodeCode: "CH-01",
      nodeName: "有理数",
      nodeType: "CHAPTER",
      sortOrder: 1
    },
    {
      nodeId: "node-knowledge-1",
      textbookVersionId: "tb-rj-g9-math",
      textbookVersionName: "人教版九年级数学",
      subjectCode: "MATH",
      parentNodeId: "node-chapter-1",
      nodeCode: "KN-01",
      nodeName: "绝对值与相反数",
      nodeType: "KNOWLEDGE",
      sortOrder: 1
    }
  ],
  assets: [
    {
      assetId: "asset-1",
      curriculumNodeId: "node-knowledge-1",
      assetType: "TEXT",
      title: "绝对值基础定义",
      bodyMarkdown: "绝对值表示一个数在数轴上到原点的距离。",
      reviewStatus: "APPROVED",
      publishStatus: "PUBLISHED"
    }
  ],
  questions: [
    {
      questionId: "question-1",
      questionType: "CHOICE",
      difficultyLevel: "EASY",
      stemMarkdown: "|-3| 的值为（ ）",
      gradeCode: "GRADE_9",
      subjectCode: "MATH"
    },
    {
      questionId: "question-2",
      questionType: "FILL",
      difficultyLevel: "MEDIUM",
      stemMarkdown: "若 a=-5，则 a 的相反数为 ____。",
      gradeCode: "GRADE_9",
      subjectCode: "MATH"
    }
  ]
};
