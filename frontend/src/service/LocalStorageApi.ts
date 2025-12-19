import type {User} from "@/model/User.ts";
import type {Question} from "@/model/Question.ts";
import type {AnswerOption} from "@/model/AnswerOption.ts";

export class LocalStorageApi {

  public getVar<T>(key: string): T | undefined {
    let item = localStorage.getItem(key);

    if (item) {
      return JSON.parse(item) as T;
    }

    return undefined
  }

  public getUserInformation(): User | undefined {
    return this.getVar<User>("user");
  }

  public setUserInformation(user: User) {
    localStorage.setItem("user", JSON.stringify(user));
  }

  public getQuestion(id: string): Question | undefined {
    return this.getVar<Question>(`question-${id}`);
  }

  public setQuestion(id: string, question: Question) {
    return localStorage.setItem(`question-${id}`, JSON.stringify(question));
  }

  public getAnswers(id: string): AnswerOption[] | undefined {
    return this.getVar<AnswerOption[]>(`question-answer-${id}`);
  }

  public setAnswers(id: string, answers: AnswerOption[]) {
    return localStorage.setItem(`question-answer-${id}`, JSON.stringify(answers));
  }
}