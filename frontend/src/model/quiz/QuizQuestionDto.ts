import type { QuizAnswerDto } from "./QuizAnswerDto";

export interface QuizQuestionDto {
    id: string;
    text: string;
    textAlternative: string | null;
    imageId: string;
    imageAlternativeId: string | null;
    answers: QuizAnswerDto[];
}