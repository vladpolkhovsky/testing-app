export interface Quiz {
  id: string | null;
  title: string;
  questions: Question[];
}

export interface Question {
  id: string | null;
  text: string;
  order: number;
  textAlternative: string | null;
  imageId: string | null;
  imageAlternativeId: string | null;
  answers: Answer[];
}

export interface Answer {
  id: string | null;
  text: string;
  isCorrect: boolean;
}


export interface QuizEditor extends Quiz {
  questions: QuestionEditor[];
  isDirty: boolean
}

export interface QuestionEditor extends Question {
  isDirty: boolean
}