import type { Client } from "@stomp/stompjs";
import { SocketFactory } from "@/service/SocketFactory.ts";
import type { OnConnectCallback } from "@/model/types.ts";
import type { User } from "@/model/User.ts";
import type {
  OnNotifySavedCallback,
  OnShowQuestionAnswerCallback,
  OnShowQuestionCallback,
  OnShowRatingCallback,
  OnShowUpdatedRatingCallback,
  OnStartGameCallback,
  OnStartRoundCallback,
  OnStopGameCallback,
  OnStopRoundCallback,
  OnUserConnectedCallback,
  WsAnswerSavedMessage,
  WsMessage,
  WsRatingMessage,
  WsShowQuestionMessage,
  WsUserConnectedMessage,
} from "@/model/stomp-messages";

export class SocketService {
  setOnQuizInitializationMessageCallback(handleInitMessage: (message: QuizInitializationMessage) => void) {
    throw new Error("Method not implemented.");
  }
  setOnQuizShowNewQuestionMessageCallback(handleShowNewQuestionMessage: (message: QuizShowNewQuestionMessage) => void) {
    throw new Error("Method not implemented.");
  }
  setOnEndQuizQuestionCallback(handleQuizRoundStopMessage: (message: QuizRoundMessage) => void) {
    throw new Error("Method not implemented.");
  }
  setOnStartQuizQuestionCallback(handleQuizRoundStartMessage: (message: QuizRoundMessage) => void) {
    throw new Error("Method not implemented.");
  }
  private roomId: string;
  private userId?: string;
  private client: Client;

  private onConnectCallback: OnConnectCallback | null = null;

  public constructor(roomId: string, user?: User) {
    this.roomId = roomId;
    this.userId = user?.userId;

    this.client = SocketFactory.create(() => {
      this.setupMainHandlers(this.client, this.roomId, this.userId);
      if (user) this.notifyConnection(user);
      this.onConnectCallback?.call(this);
    });
  }

  public setOnConnectCallback(callback: OnConnectCallback) {
    this.onConnectCallback = callback;
  }

  private setupMainHandlers(client: Client, roomId: string, userId?: string) {
    client.subscribe(`/queue/quiz/${roomId}/updates`, (message) => {
      const pasred = JSON.parse(message.body);
      const type = (pasred as WsMessage).type;
      console.log(`Message /queue/quiz/${roomId}/updates`, pasred);
      if (type == "START_GAME")
        this.onStartGameCallback?.call(this, pasred as WsMessage);
      if (type == "START_ROUND")
        this.onStartRoundCallback?.call(this, pasred as WsMessage);
      if (type == "SHOW_QUESTION")
        this.onShowQuestionCallback?.call(this, pasred as WsShowQuestionMessage);
      if (type == "SHOW_QUESTION_ANSWER")
        this.onShowQuestionAnswerCallback?.call(this, pasred as WsMessage);
      if (type == "NOTIFY_ANSWER_SAVED")
        this.onNotifySavedCallback?.call(this, pasred as WsAnswerSavedMessage);
      if (type == "STOP_ROUND")
        this.onStopRoundCallback?.call(this, pasred as WsMessage);
      if (type == "SHOW_RATING")
        this.onShowRatingCallback?.call(this, pasred as WsRatingMessage);
      if (type == "SHOW_UPDATED_RATING")
        this.onShowUpdatedRatingCallback?.call(this, pasred as WsRatingMessage);
      if (type == "STOP_GAME")
        this.onStopGameCallback?.call(this, pasred as WsMessage);
      if (type == "USER_CONNECTED")
        this.onUserConnectedCallbackCallback?.call(this, pasred as WsUserConnectedMessage);
    });

    if (this.userId) {
      client.subscribe(`/queue/quiz/${roomId}/${userId}/updates`, (message) => {
        const pasred = JSON.parse(message.body);
        const type = (pasred as WsMessage).type;
        console.log(`Message /queue/quiz/${roomId}/${userId}/updates`, pasred);
        if (type == "NOTIFY_ANSWER_SAVED")
          this.onNotifyMySavedCallback?.call(
            this,
            pasred as WsAnswerSavedMessage
          );
      });
    }
  }

  public startQuiz() {
    console.log('start round')
    this.client.publish({
      destination: `/app/quiz/${this.roomId}/start`,
    });
  }

  public sendAnswer(questionId: string, answerId: string) {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}/${this.userId}/send`,
      body: JSON.stringify({
        answerId: answerId,
        questionId: questionId,
      }),
    });
  }

  private notifyConnection(user: User) {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}`,
      body: JSON.stringify(user),
    });
  }

  public notifyConnectionSilent() {
    this.client.publish({
      destination: `/app/quiz/${this.roomId}/silent`,
    });
  }

  public activate(): void {
    this.client.activate();
  }

  private onStartGameCallback: OnStartGameCallback | null = null;
  private onStartRoundCallback: OnStartRoundCallback | null = null;
  private onShowQuestionCallback: OnShowQuestionCallback | null = null;
  private onShowQuestionAnswerCallback: OnShowQuestionAnswerCallback | null = null;
  private onNotifySavedCallback: OnNotifySavedCallback | null = null;
  private onNotifyMySavedCallback: OnNotifySavedCallback | null = null;
  private onStopGameCallback: OnStopGameCallback | null = null;
  private onStopRoundCallback: OnStopRoundCallback | null = null;
  private onShowRatingCallback: OnShowRatingCallback | null = null;
  private onShowUpdatedRatingCallback: OnShowUpdatedRatingCallback | null = null;
  private onUserConnectedCallbackCallback: OnUserConnectedCallback | null = null;

  public setOnUserConnectedCallbackCallback(onUserConnectedCallbackCallback: OnUserConnectedCallback) {
    this.onUserConnectedCallbackCallback = onUserConnectedCallbackCallback;
  }
  
  public setOnStartGameCallback(onStartGameCallback: OnStartGameCallback) {
    this.onStartGameCallback = onStartGameCallback;
  }

  public setOnStartRoundCallback(onStartRoundCallback: OnStartRoundCallback) {
    this.onStartRoundCallback = onStartRoundCallback;
  }

  public setOnShowQuestionCallback(
    onShowQuestionCallback: OnShowQuestionCallback
  ) {
    this.onShowQuestionCallback = onShowQuestionCallback;
  }

  public setOnShowQuestionAnswerCallback(
    onShowQuestionAnswerCallback: OnShowQuestionAnswerCallback
  ) {
    this.onShowQuestionAnswerCallback = onShowQuestionAnswerCallback;
  }

  public setOnNotifySavedCallback(
    onNotifySavedCallback: OnNotifySavedCallback
  ) {
    this.onNotifySavedCallback = onNotifySavedCallback;
  }

  public setOnNotifyMySavedCallback(
    onNotifyMySavedCallback: OnNotifySavedCallback
  ) {
    this.onNotifyMySavedCallback = onNotifyMySavedCallback;
  }

  public setOnStopGameCallback(onStopGameCallback: OnStopGameCallback) {
    this.onStopGameCallback = onStopGameCallback;
  }

  public setOnStopRoundCallback(onStopRoundCallback: OnStopRoundCallback) {
    this.onStopRoundCallback = onStopRoundCallback;
  }

  public setOnShowRatingCallback(onShowRatingCallback: OnShowRatingCallback) {
    this.onShowRatingCallback = onShowRatingCallback;
  }

  public setOnShowUpdatedRatingCallback(
    onShowUpdatedRatingCallback: OnShowUpdatedRatingCallback
  ) {
    this.onShowUpdatedRatingCallback = onShowUpdatedRatingCallback;
  }
}
