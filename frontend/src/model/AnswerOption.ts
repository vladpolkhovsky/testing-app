export interface AnswerOption {
  id: string,
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

  return texts.map<AnswerOption>((text, index) => {
    return {
      id: (Math.random() * 100).toString(),
      optionText: text,
      optionVariant: 'A' + index
    }
  })
}