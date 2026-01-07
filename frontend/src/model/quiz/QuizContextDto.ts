import type { User } from "../User";
import type { QuizDto } from "./QuizDto";

export interface QuizContextDto {
    id: string;
    quiz: QuizDto;
    currentQuestionId: string;
    currentRound: number;
    maxRounds: number;
    gameStarted: boolean;
    roundStarted: boolean;
    gameFinished: boolean;
    participants: User[];
    questionOrder: string[];
    questionToUserToAnswer: Record<string, Record<string, string>>;
    questionToUserToAnswerTime: Record<string, Record<string, Date>>;
    questionToUserToRating: Record<string, Record<string, number>>;
}