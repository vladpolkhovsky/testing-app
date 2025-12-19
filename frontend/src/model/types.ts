import type {frameCallbackType} from "@stomp/stompjs";
import type {RatingItem} from "@/model/RatingItem.ts";
import type {Question} from "@/model/Question.ts";
import type {AnswerOption} from "@/model/AnswerOption.ts";

export type Optional<T> = T | null;
export type OnConnectCallback = () => void;
export type OnStompErrorCallback = frameCallbackType;
export type UpdateRatingCallback = (items: RatingItem[]) => void;
export type OnQuizInitializationMessageCallback = (question: Question, answers: AnswerOption[], ratingItems: RatingItem[], questionId: string, nextQuestionId?: string) => void;
export type OnStartQuizQuestionCallback = (question: Question, answers: AnswerOption[], diration: number) => void;
export type OnEndQuizQuestionCallback = (question: Question, answers: AnswerOption[]) => void;
export type OnQuizUpdateRatingMessageCallback = (ratingItems: RatingItem[]) => void;