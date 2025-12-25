import type {Question} from "@/model/Question.ts";
import type {AnswerOption} from "@/model/AnswerOption.ts";
import type {RatingItem} from "@/model/RatingItem.ts";

export interface QuizInitializationMessage extends QuizMessage {
  type: "INIT_MESSAGE",

  title: string;

  currentRound: number;
  maxRounds: number;

  question: Question,
  answers: AnswerOption[],
  ratingItems: RatingItem[],

  questionId: string,
  nextQuestionId?: string,

  gameStarted: boolean,
  roundStarted: boolean,
  gameFinished: boolean
}

export interface QuizShowNewQuestionMessage extends QuizMessage {
  type: "NEW_QUESTION",

  question: Question,
  answers: AnswerOption[],

  questionId: string,
  nextQuestionId?: string
}

export interface QuizRoundMessage extends QuizMessage {
  type: "START_ROUND" | "STOP_ROUND",
  questionId: string,

  imageAlternativeId?: string,
  textAlternative?: string,

  nextRound: number,
  gameFinished: boolean,

  duration: number
}

export interface QuizNewRatingMessage extends QuizMessage {
  type: "NEW_RATING",
  ratingItems: RatingItem[]
}

export interface QuizAnswerSavedMessage extends QuizMessage {
  type: "ANSWER_SAVED",
  username: string
}

export interface QuizMessage {
  type: QuizMessageType,
  quizId: string
}

export type QuizMessageType = "INIT_MESSAGE" | "NEW_QUESTION" | "START_ROUND" | "STOP_ROUND" | "NEW_RATING" | "ANSWER_SAVED";


