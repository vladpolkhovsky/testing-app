import type {User} from "@/model/User.ts";

export interface RatingItem extends User {
  rating: number
}

export function getRandomRating(): RatingItem[] {
  const text = [
    "Вася",
    "Федя",
    "Владислав",
    "Владислав Полховский",
    "Даша",
    "Дашуля",
    "Дашуля Кухновец",
    "Сери",
    "Серикс",
    "Колесник Сергей"
  ]

  return text.map<RatingItem>(item => {
    return {
      rating: Math.ceil(Math.random() * 1000),
      userId: [...item].reduce<number>((previousValue, currentValue) => previousValue + currentValue.charCodeAt(0), 0).toString(),
      username: item
    }
  })
}