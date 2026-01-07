import type { User } from "./User";

export type OnStartGameCallback = (message: WsMessage) => void;
export type OnStartRoundCallback = (message: WsMessage) => void;
export type OnShowQuestionCallback = (message: WsShowQuestionMessage) => void;
export type OnShowQuestionAnswerCallback = (message: WsMessage) => void;
export type OnNotifySavedCallback = (message: WsAnswerSavedMessage) => void;
export type OnStopGameCallback = (message: WsMessage) => void;
export type OnStopRoundCallback = (message: WsMessage) => void;
export type OnShowRatingCallback = (message: WsRatingMessage) => void;
export type OnShowUpdatedRatingCallback = (message: WsRatingMessage) => void;
export type OnUserConnectedCallback = (message: WsUserConnectedMessage) => void;

export interface WsUserConnectedMessage extends WsMessage {
  type: "USER_CONNECTED";
  user: User
}

export interface WsShowQuestionMessage extends WsMessage {
  type: "SHOW_QUESTION";
  questionId: string;
  currentRound: number;
  roundTime: number;
}

export interface WsRatingMessage extends WsMessage {
  type: "SHOW_RATING" | "SHOW_UPDATED_RATING";
  rating: Record<string, number>;
}

export interface WsAnswerSavedMessage extends WsMessage {
  type: "NOTIFY_ANSWER_SAVED";
  contextId: string;
  userId: string;
  questionId: string;
  answerId: string;
  text: string;
}

export interface WsMessage {
  type: WsMessageType;
}

export type WsMessageType =
  | "START_GAME"
  | "START_ROUND"
  | "SHOW_QUESTION"
  | "SHOW_QUESTION_ANSWER"
  | "NOTIFY_ANSWER_SAVED"
  | "STOP_ROUND"
  | "SHOW_RATING"
  | "SHOW_UPDATED_RATING"
  | "STOP_GAME"
  | "USER_CONNECTED";
