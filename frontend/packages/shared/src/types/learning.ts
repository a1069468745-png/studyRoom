export interface CurriculumNodeDictionaryItem {
  nodeId: string;
  textbookVersionId: string;
  textbookVersionName: string;
  subjectCode: string;
  parentNodeId: string | null;
  nodeCode: string;
  nodeName: string;
  nodeType: string;
  sortOrder: number;
}

export interface ContentAssetItem {
  assetId: string;
  curriculumNodeId: string;
  assetType: string;
  title: string;
  bodyMarkdown: string;
  reviewStatus: string;
  publishStatus: string;
}

export interface ClientQuestionItem {
  questionId: string;
  questionType: string;
  difficultyLevel: string;
  stemMarkdown: string;
  gradeCode: string;
  subjectCode: string;
}
