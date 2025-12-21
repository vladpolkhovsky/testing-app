import gsap from "gsap";

export class SoundEngine {
  private static CURRENT: HTMLAudioElement | null = null;
  private static currentFadeTween: gsap.core.Tween | null = null;
  private static isChangingTrack: boolean = false;
  private static nextTrackTimeout: NodeJS.Timeout | null = null;

  private static readonly sounds: SoundMetadata[] = [
    sound("/kahoot-01-main.mp3", true),
    sound("/kahoot-02-main.mp3", true),
    sound("/kahoot-03-main.m4a", true),
    sound("/kahoot-04.m4a"),
    sound("/kahoot-05.m4a"),
    sound("/kahoot-06.m4a"),
    sound("/kahoot-07.m4a")
  ];

  private static playQueue: string[] = [];

  public static initialize() {
    // Добавляем обработчик клика на весь документ для первого взаимодействия
    document.addEventListener('click', SoundEngine.handleFirstInteraction, { once: true });
  }

  private static handleFirstInteraction() {
    // Создаем аудиоэлемент при первом взаимодействии
    SoundEngine.CURRENT = new Audio();
    SoundEngine.CURRENT.volume = 0;

    // Добавляем обработчики событий
    SoundEngine.setupAudioEventListeners();

    // Запускаем первый трек
    SoundEngine.nextTrack(true);
  }

  private static setupAudioEventListeners() {
    if (!SoundEngine.CURRENT) return;

    // Обработчик окончания трека
    SoundEngine.CURRENT.addEventListener("ended", () => {
      SoundEngine.nextTrack();
    });

    // Обработчик для автоматического переключения за 3 секунды до конца
    SoundEngine.CURRENT.addEventListener("timeupdate", () => {
      SoundEngine.handleTimeUpdate();
    });
  }

  private static handleTimeUpdate() {
    if (!SoundEngine.CURRENT || SoundEngine.CURRENT.duration === 0) return;

    const timeRemaining = SoundEngine.CURRENT.duration - SoundEngine.CURRENT.currentTime;
    const fadeDuration = 3000; // 3 секунды

    // Если до конца осталось 3 секунды и еще не запланировано переключение
    if (timeRemaining <= 5 && !SoundEngine.nextTrackTimeout && timeRemaining > 0) {
      SoundEngine.nextTrackTimeout = setTimeout(() => {
        SoundEngine.nextTrack();
        SoundEngine.nextTrackTimeout = null;
      }, Math.max(0, timeRemaining * 1000 - fadeDuration));
    }
  }

  public static nextTrack(main: boolean = false) {
    const soundMetadata = SoundEngine.chooseSound(main);
    SoundEngine.comfortChangeTrack(soundMetadata);
  }

  private static chooseSound(main: boolean = false): SoundMetadata {
    const lastPlayed = new Set<string>();

    // Получаем последние 5 воспроизведенных треков
    SoundEngine.playQueue.slice(-5).forEach(sound => {
      lastPlayed.add(sound);
    });

    // Создаем перемешанный список треков
    const shuffledSounds = [...SoundEngine.sounds].sort(() => Math.random() - 0.5);

    let newSound: SoundMetadata | null = null;

    // Сначала ищем трек, который давно не играл
    for (const sound of shuffledSounds) {
      if (main && !sound.canBePlayedOnStart) continue;
      if (!lastPlayed.has(sound.src)) {
        newSound = sound;
        break;
      }
    }

    // Если не нашли, берем любой подходящий
    if (!newSound) {
      for (const sound of shuffledSounds) {
        if (main && !sound.canBePlayedOnStart) continue;
        newSound = sound;
        break;
      }
    }

    // Добавляем в историю воспроизведения
    if (newSound) {
      SoundEngine.playQueue.push(newSound.src);

      // Ограничиваем историю последними 10 треками
      if (SoundEngine.playQueue.length > 10) {
        SoundEngine.playQueue.shift();
      }
    }

    return newSound!;
  }

  private static async comfortChangeTrack(soundMetadata: SoundMetadata) {
    // Если уже идет смена трека, игнорируем новый вызов
    if (SoundEngine.isChangingTrack) return;

    SoundEngine.isChangingTrack = true;

    try {
      // Если есть текущий трек и он играет, плавно выключаем его
      if (SoundEngine.CURRENT && !SoundEngine.CURRENT.paused) {
        await SoundEngine.fadeOutCurrentTrack();

        // Останавливаем текущий трек
        SoundEngine.CURRENT.pause();
        SoundEngine.CURRENT.currentTime = 0;
      }

      // Создаем новый аудиоэлемент, если его нет
      if (!SoundEngine.CURRENT) {
        SoundEngine.CURRENT = new Audio();
        SoundEngine.CURRENT.volume = 0;
        SoundEngine.setupAudioEventListeners();
      }

      // Устанавливаем новый источник
      SoundEngine.CURRENT.src = soundMetadata.src;

      // Очищаем предыдущий таймер, если был
      if (SoundEngine.nextTrackTimeout) {
        clearTimeout(SoundEngine.nextTrackTimeout);
        SoundEngine.nextTrackTimeout = null;
      }

      // Запускаем воспроизведение с плавным увеличением громкости
      await SoundEngine.CURRENT.play();
      await SoundEngine.fadeInCurrentTrack();

    } catch (error) {
      console.error("Ошибка при смене трека:", error);
    } finally {
      SoundEngine.isChangingTrack = false;
    }
  }

  private static fadeOutCurrentTrack(): Promise<void> {
    return new Promise((resolve) => {
      if (!SoundEngine.CURRENT || SoundEngine.CURRENT.paused) {
        resolve();
        return;
      }

      // Останавливаем предыдущую анимацию, если есть
      if (SoundEngine.currentFadeTween) {
        SoundEngine.currentFadeTween.kill();
      }

      SoundEngine.currentFadeTween = gsap.to(SoundEngine.CURRENT, {
        volume: 0,
        duration: 1.5,
        onComplete: () => {
          resolve();
        }
      });
    });
  }

  private static fadeInCurrentTrack(): Promise<void> {
    return new Promise((resolve) => {
      if (!SoundEngine.CURRENT) {
        resolve();
        return;
      }

      // Останавливаем предыдущую анимацию, если есть
      if (SoundEngine.currentFadeTween) {
        SoundEngine.currentFadeTween.kill();
      }

      SoundEngine.currentFadeTween = gsap.to(SoundEngine.CURRENT, {
        volume: 1,
        duration: 3,
        onComplete: () => {
          resolve();
        }
      });
    });
  }

  // Метод для принудительной смены трека (например, по кнопке)
  public static playTrackImmediately(src: string) {
    const soundMetadata: SoundMetadata = {
      src,
      canBePlayedOnStart: true
    };
    SoundEngine.comfortChangeTrack(soundMetadata);
  }

  // Метод для остановки музыки с затуханием
  public static async stopMusic() {
    if (SoundEngine.CURRENT && !SoundEngine.CURRENT.paused) {
      await SoundEngine.fadeOutCurrentTrack();
      SoundEngine.CURRENT.pause();
      SoundEngine.CURRENT.currentTime = 0;

      // Очищаем таймер автоматического переключения
      if (SoundEngine.nextTrackTimeout) {
        clearTimeout(SoundEngine.nextTrackTimeout);
        SoundEngine.nextTrackTimeout = null;
      }
    }
  }
}

export interface SoundMetadata {
  src: string;
  canBePlayedOnStart: boolean;
}

function sound(src: string, canBePlayedOnStart: boolean = false): SoundMetadata {
  return {
    src,
    canBePlayedOnStart,
  };
}

(window as any).SoundEngine = SoundEngine;