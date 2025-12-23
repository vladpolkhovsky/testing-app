export interface Quiz {
  id: string | null;
  title: string;
  questions: Question[];
}

export interface Question {
  text: string;
  textAlternative: string | null;
  imageId: string | null;
  imageAlternativeId: string | null;
  answers: Answer[];
}

export interface Answer {
  optionText: string;
  isCorrect: boolean;
}

export interface QuizEditor extends Quiz {
  questions: QuestionEditor[];
  hasErrorInTitle: boolean;
  isTitleDirty: boolean,
  isDirty: boolean
}

export interface QuestionEditor extends Question {
  hasErrorInText: boolean;
  viewId: string;
  hasErrorInTextAlternative: boolean;
  hasNoCorrectAnswer: boolean;
  answers: AnswerEditor[];
  isDirty: boolean;
}

export interface AnswerEditor extends Answer {
  textValidationError: boolean;
  viewId: string;
}

export function isNotSavableQuiz(quiz: QuizEditor): boolean {
  if (quiz.hasErrorInTitle) {
    return true;
  }

  let isNotSavable = false;

  quiz.questions.forEach((question: QuestionEditor) => {
    isNotSavable ||= question.hasNoCorrectAnswer || question.hasErrorInTextAlternative || question.hasErrorInText;
    question.answers.forEach((answer: AnswerEditor) => {
      isNotSavable ||= answer.textValidationError;
    });
  });

  return isNotSavable;
}