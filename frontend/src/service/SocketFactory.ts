import {Client, Stomp} from "@stomp/stompjs";
import SockJS from "sockjs-client";
import type {OnConnectCallback} from "@/model/types.ts";

export class SocketFactory {

  private static readonly SERVER_URL: string = "/api/quiz/ws";

  public static create(onConnectCallback: OnConnectCallback): Client {
    const socket = new SockJS(SocketFactory.SERVER_URL);

    return new Client({
      webSocketFactory: () => socket,
      debug: (msg: string) => console.debug(msg),
      onConnect: frame => {
        console.log("Connected", frame);
        onConnectCallback();
      },
      onWebSocketClose: (err: any) => {
        console.error(err);
        alert("Потеря соединения. Перезагрузи страницу.");
      },
      onWebSocketError: (err: any) => {
        console.error(err);
        alert("Потеря соединения. Перезагрузи страницу.");
      },
      onStompError: () => {
        alert("Потеря соединения. Перезагрузи страницу.");
      },
      reconnectDelay: 300,
      connectionTimeout: 3000
    });
  }
}