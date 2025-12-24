export interface AnswerOption {
  id: string,
  correct: boolean,
  optionVariant: string,
  optionText: string
}

export function getRandomAnswers(): AnswerOption[] {
  const texts = [
    "Lorem ipsum dolor sit amet consectetur.",
    "Dolor sit amet consectetur.",
    "Amet consectetur.",
    "Lorem ipsum dolor.",
    "Ipsum dolor sit amet.",
    "1905"
  ];

  return texts.sort((a, b) => Math.random() - 0.5).map<AnswerOption>((text, index) => {
    return {
      id: (Math.random() * 100).toString(),
      optionText: text,
      optionVariant: String.fromCharCode('A'.charCodeAt(0) + index),
      correct: index == 0
    }
  })
}