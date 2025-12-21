import type {Client} from "@stomp/stompjs";
import {SocketFactory} from "@/service/SocketFactory.ts";
import type {
  OnConnectCallback, OnEndQuizQuestionCallback, OnQuizInitializationMessageCallback,
  OnQuizShowNewQuestionMessageCallback, OnQuizUpdateRatingMessageCallback,
  OnStartQuizQuestionCallback
} from "@/model/types.ts";
import type {User} from "@/model/User.ts";
import type {
  QuizInitializationMessage,
  QuizMessage, QuizNewRatingMessage,
  QuizRoundMessage,
  QuizShowNewQuestionMessage
} from "@/model/stomp-messages.ts";

export class SocketService {

  private roomId: string;
  private user?: User;
  private client: Client;
  private onConnectCallback: OnConnectCallback | null = null;
  private handleQuizInitializationMessage?: OnQuizInitializationMessageCallback;
  private handleQuizShowNewQuestionMessageCallback?: OnQuizShowNewQuestionMessageCallback;
  private handleStartQuizQuestionCallback?: OnStartQuizQuestionCallback;
  private handleEndQuizQuestionCallback?: OnEndQuizQuestionCallback;
  private handleQuizUpdateRatingMessageCallback?: OnQuizUpdateRatingMessageCallback;

  public constructor(roomId: string, user?: User) {
    this.roomId = roomId;
    this.user = user;

    this.client = SocketFactory.create(() => {
      this.setupMainHandlers(this.roomId, this.client);

      if (this.user) this.notifyConnection();

      this.onConnectCallback?.call(this);
    });
  }

  public setOnConnectCallback(callback: OnConnectCallback) {
    this.onConnectCallback = callback;
  }

  private setupMainHandlers(roomId: string, client: Client) {
    client.subscribe(`/queue/quiz/${roomId}/updates`, message => {
      const pasred = JSON.parse(message.body);
      const type = (pasred as QuizMessage).type;
      console.log(`Message /queue/quiz/${roomId}/updates`, pasred);

      if (type == "INIT_MESSAGE") {
        const typedMessage = pasred as QuizInitializationMessage
        this.handleQuizInitializationMessage?.call(this, typedMessage);
      }

      if (type == "NEW_QUESTION") {
        const typedMessage = pasred as QuizShowNewQuestionMessage
        this.handleQuizShowNewQuestionMessageCallback?.call(this, typedMessage);
      }

      if (type == "START_ROUND") {
        const typedMessage = pasred as QuizRoundMessage
        this.handleStartQuizQuestionCallback?.call(this, typedMessage);
      }

      if (type == "STOP_ROUND") {
        const typedMessage = pasred as QuizRoundMessage
        this.handleEndQuizQuestionCallback?.call(this, typedMessage);
      }

      if (type == "NEW_RATING") {
        const typedMessage = pasred as QuizNewRatingMessage
        this.handleQuizUpdateRatingMessageCallback?.call(this, typedMessage);
      }
    });
  }

  public startQuiz() {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}/start`
    });
  }

  public sendAnswer(id: string) {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}/${this.user?.userId}/send`,
      body: JSON.stringify({
        answerId: id
      }),
    });
  }

  private notifyConnection() {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}`,
      body: JSON.stringify(this.user)
    });
  }

  public notifyConnectionSilent() {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}/silent`
    });
  }

  public activate(): void {
    this.client.activate();
  }

  public setOnQuizInitializationMessageCallback(handleQuizInitializationMessage: OnQuizInitializationMessageCallback) {
    this.handleQuizInitializationMessage = handleQuizInitializationMessage;
  }

  public setOnStartQuizQuestionCallback(handleStartQuizQuestionCallback: OnStartQuizQuestionCallback) {
    this.handleStartQuizQuestionCallback = handleStartQuizQuestionCallback;
  }

  public setOnEndQuizQuestionCallback(handleEndQuizQuestionCallback: OnEndQuizQuestionCallback) {
    this.handleEndQuizQuestionCallback = handleEndQuizQuestionCallback;
  }

  public setOnQuizShowNewQuestionMessageCallback(handleQuizShowNewQuestionMessageCallback: OnQuizShowNewQuestionMessageCallback) {
    this.handleQuizShowNewQuestionMessageCallback = handleQuizShowNewQuestionMessageCallback;
  }

  public setOnQuizUpdateRatingMessageCallback(handleQuizUpdateRatingMessageCallback: OnQuizUpdateRatingMessageCallback) {
    this.handleQuizUpdateRatingMessageCallback = handleQuizUpdateRatingMessageCallback;
  }
}