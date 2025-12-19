import SockJS from 'sockjs-client';
import type { User } from "@/model/User.ts";

import type {
  OnConnectCallback, OnStartQuizQuestionCallback,
  OnStompErrorCallback,
  Optional
} from "@/model/types.ts";

import { Client } from "@stomp/stompjs";

import type {
  QuizInitializationMessage,
  QuizMessage,
  QuizMessageType,
  QuizShowNewQuestionMessage,
  QuizRoundMessage,
  QuizNewRatingMessage
} from "@/model/stomp-messages.ts";

export type MessageHandler<T extends QuizMessage> = {
  type: QuizMessageType;
  parser: (data: any) => T;
  callback: (message: T) => void;
};

export class SocketService {
  private roomId: string | null = null;
  private user: User | null = null;
  private client: Client | null = null;
  private onConnectCallback: OnConnectCallback | null = null;
  private onStompErrorCallback: OnStompErrorCallback | null = null;
  private messageHandlers = new Map<QuizMessageType, MessageHandler<any>>();

  public constructor(roomId: string, user?: User) {
    this.roomId = roomId;
    this.user = user ?? null;
  }

  public setOnConnectedCallback(callback: OnConnectCallback): void {
    this.onConnectCallback = callback;
  }

  public setOnStompErrorCallback(callback: OnStompErrorCallback): void {
    this.onStompErrorCallback = callback;
  }

  public registerMessageHandler<T extends QuizMessage>(
    type: QuizMessageType,
    callback: (message: T) => void
  ): void {
    this.messageHandlers.set(type, {
      type: type,
      parser: obj => obj as T,
      callback: callback
    });
  }

  public connect(): void {
    if (!this.roomId) {
      throw new Error("Room ID is not set");
    }

    this.client = SocketService.createClient(
      this.onConnectCallback,
      this.onStompErrorCallback
    );

    SocketService.configureMessageHandlers(
      this.client,
      this.roomId,
      this.messageHandlers
    );

    this.client.activate();
  }

  public disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
  }

  public sendAnswer(questionId: string, answerId: string): void {
    if (!this.client || !this.client.connected) {
      console.error("Socket is not connected");
      return;
    }

    this.client.publish({
      destination: `/app/quiz/${this.roomId}/answer`,
      body: JSON.stringify({
        questionId,
        answerId,
        userId: this.user?.userId
      })
    });
  }

  public startGame() {

  }

  private static createClient(
    onConnectCallback: Optional<OnConnectCallback>,
    onStompErrorCallback: Optional<OnStompErrorCallback>
  ): Client {
    return new Client({
      webSocketFactory: () => new SockJS("/ws"),
      debug: str => console.log('debug', str),
      reconnectDelay: 500,
      heartbeatIncoming: 1000,
      heartbeatOutgoing: 1000,
      onConnect: onConnectCallback ?? undefined,
      onStompError: onStompErrorCallback ?? undefined
    });
  }

  private static configureMessageHandlers(
    client: Client,
    roomId: string,
    handlers: Map<QuizMessageType, MessageHandler<any>>
  ): void {
    client.subscribe(`/topic/quiz/${roomId}`, message => {
      try {
        const rawMessage = JSON.parse(message.body);
        const quizMessage = rawMessage as QuizMessage;
        const handler = handlers.get(quizMessage.type);

        if (handler) {
          const typedMessage = handler.parser(rawMessage);
          handler.callback(typedMessage);
        } else {
          console.warn(`No handler registered for message type: ${quizMessage.type}`);
        }
      } catch (error) {
        console.error('Error processing message:', error);
      }
    });
  }
}