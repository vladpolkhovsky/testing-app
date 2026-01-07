import type { QuizQuestionDto } from "./QuizQuestionDto";

export interface QuizDto {
    id: string;
    title: string;
    questions: QuizQuestionDto[];
}