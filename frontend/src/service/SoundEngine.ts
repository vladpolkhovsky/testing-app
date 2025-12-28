import gsap from "gsap";

export class SoundEngine {
  private static FADE_IN_DURATION = 1;
  private static FADE_OUT_DURATION = 1;
  private static BEFORE_END_TIME_TO_CHANGE = 3;

  private static IS_TRACK_IN_CHANGING = false;

  private static CURRENT: HTMLAudioElement | null = null;

  private static readonly sounds: SoundMetadata[] = [
    sound("/01.m4a"),
    sound("/02.m4a"),
    sound("/03.m4a"),
    sound("/04.m4a"),
    sound("/05.m4a"),
    sound("/06.m4a"),
    sound("/07.m4a"),
    sound("/08.m4a"),
    sound("/09.m4a"),
    sound("/10.m4a"),
    sound("/11.m4a"),
    sound("/12.m4a"),
    sound("/13.m4a"),
    sound("/14.m4a"),
    sound("/15.m4a"),
    sound("/16.m4a"),
    sound("/17.m4a"),
    sound("/18.m4a"),
    sound("/19.m4a"),
    sound("/20.m4a"),
    sound("/21.m4a"),
    sound("/22.m4a")
  ];

  private static sound_queue_index = 0;
  private static readonly sound_queue = [
    ...SoundEngine.sounds.sort((a, b) => Math.random() - 0.5).sort((a, b) => Math.random() - 0.5),
    ...SoundEngine.sounds.sort((a, b) => Math.random() - 0.5).sort((a, b) => Math.random() - 0.5),
    ...SoundEngine.sounds.sort((a, b) => Math.random() - 0.5).sort((a, b) => Math.random() - 0.5)
  ];

  public static initialize() {
    // Добавляем обработчик клика на весь документ для первого взаимодействия
    document.addEventListener('click', SoundEngine.handleFirstInteraction, {once: true});
  }

  private static handleFirstInteraction() {
    SoundEngine.comfortChangeTrack(SoundEngine.chooseSound());
  }

  private static setupAudioEventListeners() {
      SoundEngine.CURRENT?.addEventListener("timeupdate", SoundEngine.handleTimeUpdate);
  }

  private static handleTimeUpdate() {
    if (SoundEngine.IS_TRACK_IN_CHANGING) return;
    //console.log(SoundEngine.CURRENT!.duration, SoundEngine.CURRENT!.currentTime)
    if (SoundEngine.CURRENT!.duration - SoundEngine.BEFORE_END_TIME_TO_CHANGE < SoundEngine.CURRENT!.currentTime) {
      SoundEngine.nextTrack();
    }
  }

  // затухание предыдущего + плавное начало нового
  private static async comfortChangeTrack(soundMetadata: SoundMetadata) {
    if (SoundEngine.IS_TRACK_IN_CHANGING) return;

    await SoundEngine.stopMusic();

    console.log("start fade in new track")

    SoundEngine.CURRENT = new Audio(soundMetadata.src);
    SoundEngine.CURRENT.volume = 0;

    SoundEngine.setupAudioEventListeners();

    await SoundEngine.CURRENT.play();

    await gsap.to(SoundEngine.CURRENT, {
      volume: 1,
      duration: this.FADE_IN_DURATION
    });

    console.log("current track now played")

    SoundEngine.IS_TRACK_IN_CHANGING = false;
  }

  // Метод для остановки музыки с затуханием
  public static async stopMusic() {
    if (SoundEngine.IS_TRACK_IN_CHANGING) return;
    SoundEngine.IS_TRACK_IN_CHANGING = true;

    if (SoundEngine.CURRENT) {
      console.log("start fade out track")

      await gsap.to(SoundEngine.CURRENT, {
        volume: 0,
        duration: this.FADE_OUT_DURATION
      });

      SoundEngine.CURRENT.pause();

      console.log("current track stopped")
    }
  }

  public static nextTrack() {
    const soundMetadata = SoundEngine.chooseSound();
    SoundEngine.comfortChangeTrack(soundMetadata);
  }

  private static chooseSound(): SoundMetadata {
    const index = SoundEngine.sound_queue_index;
    SoundEngine.sound_queue_index = (SoundEngine.sound_queue_index + 1) % SoundEngine.sound_queue.length;
    return SoundEngine.sound_queue[index]!;
  }

  public static playTrackImmediately(src: string) {
    const soundMetadata: SoundMetadata = {src};
    SoundEngine.comfortChangeTrack(soundMetadata);
  }
}

export interface SoundMetadata {
  src: string;
}

function sound(src: string): SoundMetadata {
  return {src: src};
}

(window as any).SoundEngine = SoundEngine;