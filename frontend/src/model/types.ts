import type {frameCallbackType} from "@stomp/stompjs";
import type {RatingItem} from "@/model/RatingItem.ts";
import type {Question} from "@/model/Question.ts";
import type {AnswerOption} from "@/model/AnswerOption.ts";
import type {
  QuizAnswerSavedMessage,
  QuizInitializationMessage,
  QuizNewRatingMessage,
  QuizRoundMessage,
  QuizShowNewQuestionMessage
} from "@/model/stomp-messages.ts";

export type Optional<T> = T | null;
export type OnConnectCallback = () => void;
export type OnStompErrorCallback = frameCallbackType;
export type UpdateRatingCallback = (items: RatingItem[]) => void;
export type OnQuizInitializationMessageCallback = (message: QuizInitializationMessage) => void;
export type OnQuizShowNewQuestionMessageCallback = (message: QuizShowNewQuestionMessage) => void;
export type OnStartQuizQuestionCallback = (message: QuizRoundMessage) => void;
export type OnEndQuizQuestionCallback = (message: QuizRoundMessage) => void;
export type OnQuizUpdateRatingMessageCallback = (message: QuizNewRatingMessage) => void;
export type OnQuizAnswerSavedMessageCallback = (message: QuizAnswerSavedMessage) => void;