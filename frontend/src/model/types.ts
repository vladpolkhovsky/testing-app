import type {frameCallbackType} from "@stomp/stompjs";

export type Optional<T> = T | null;
export type OnConnectCallback = () => void;
export type OnStompErrorCallback = frameCallbackType;