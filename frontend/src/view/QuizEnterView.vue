<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import {useRoute, useRouter} from "vue-router";
import {inject, onMounted, ref} from "vue";
import type {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import {User, LogIn, RectangleEllipsis} from "lucide-vue-next";
import {Toaster, toast} from "vue-sonner";

const route = useRoute();
const router = useRouter();
const username = ref<string>("");
const password = ref<string>("");
const localStorageApi = inject<LocalStorageApi>("LocalStorageApi")!!;
const isLoading = ref(false);

function onLogin() {
  fetch("/api/quiz/user/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: username.value,
      password: password.value,
    })
  }).then(res => new Promise((resolve, reject) => {
    if (res.status == 200) resolve(res);
    else if (res.status == 404) reject("Логин или пароль неправильные");
    else reject(`Ошибка: Код ${res.status}`);
  })).then(() => {
    router.push("/");
  }).catch((str: string) => {
    toast.error(str);
  });
}

function onRegister() {
  fetch("/api/quiz/user/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      username: username.value,
      password: password.value,
    })
  }).then(res => new Promise((resolve, reject) => {
    if (res.status == 200) resolve(res);
    else if (res.status == 404) reject("Логин или пароль неправильные");
    else reject(`Ошибка: Код ${res.status}`);
  })).then(() => {
    router.push("/");
  }).catch((str: string) => {
    toast.error(str);
  });
}

// Автофокус на поле ввода при загрузке
onMounted(() => {
  const input = document.getElementById('username');
  input?.focus();
});
</script>

<template>
  <div class="w-full max-w-2xl mx-auto mt-6">
    <LiquidGlass class="bg-white/70 w-full max-w-sm sm:max-w-md mx-auto p-6 sm:p-8 rounded-2xl">
      <!-- Header -->
      <div class="text-center mb-6 sm:mb-8">
        <div
            class="inline-flex items-center justify-center w-16 h-16 sm:w-20 sm:h-20 rounded-full bg-gradient-to-r from-blue-500 to-purple-500 mb-4">
          <User :size="32" class="sm:w-10 sm:h-10 text-white"/>
        </div>
        <h1 class="text-2xl sm:text-3xl font-bold text-gray-800 mb-2">
          Логин
        </h1>
      </div>

      <!-- Login Form -->
      <form @submit.prevent="() => {}" class="space-y-4 sm:space-y-5">
        <!-- Username Input -->
        <div class="space-y-2">
          <label for="username" class="block text-sm font-medium text-gray-700">
            Имя пользователя
          </label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <User :size="18" class="text-gray-400"/>
            </div>
            <input
                type="text"
                id="username"
                v-model="username"
                required
                :disabled="isLoading"
                maxlength="30"
                class="block w-full pl-10 pr-4 py-3 sm:py-3.5 border-2 border-gray-300 rounded-xl text-sm sm:text-base placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                placeholder="Имя пользователя"
                autocomplete="off"
                autocapitalize="words"
            />
          </div>
          <div class="space-y-2">
            <label for="username" class="block text-sm font-medium text-gray-700">
              Пароль
            </label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <RectangleEllipsis :size="18" class="text-gray-400"/>
              </div>
              <input
                  type="password"
                  id="password"
                  v-model="password"
                  required
                  :disabled="isLoading"
                  maxlength="30"
                  class="block w-full pl-10 pr-4 py-3 sm:py-3.5 border-2 border-gray-300 rounded-xl text-sm sm:text-base placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                  placeholder="Пароль"
                  autocomplete="off"
              />
            </div>
          </div>
        </div>

        <!-- Submit Button -->
        <button type="submit" @click="onLogin()" :disabled="!username.trim() || !password.trim() || isLoading" :class="[
            'w-full py-3 sm:py-4 px-4 rounded-xl font-bold text-sm sm:text-base transition-all duration-200 active:scale-[0.98] touch-manipulation',
            'flex items-center justify-center gap-2',
            'disabled:opacity-50 disabled:cursor-not-allowed',
            !isLoading ? 'bg-gradient-to-r from-emerald-500 to-emerald-600 text-white hover:from-emerald-600 hover:to-emerald-700 hover:shadow-lg' : 'bg-gradient-to-r from-gray-400 to-gray-500 text-gray-800'
          ]"
                style="min-height: 44px;">
          <LogIn v-if="!isLoading" :size="18" class="sm:w-5 sm:h-5"/>
          <svg v-if="isLoading"
               class="w-5 h-5 animate-spin"
               fill="none"
               viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
          </svg>
          <span>
            {{ isLoading ? 'Вход...' : 'Войти' }}
          </span>
        </button>

        <!-- Submit Button -->
        <button type="submit" @click="onRegister()" :disabled="!username.trim() || !password.trim() || isLoading"
                :class="[
            'w-full py-3 sm:py-4 px-4 rounded-xl font-bold text-sm sm:text-base transition-all duration-200 active:scale-[0.98] touch-manipulation',
            'flex items-center justify-center gap-2',
            'disabled:opacity-50 disabled:cursor-not-allowed',
            !isLoading ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white hover:from-blue-600 hover:to-blue-700 hover:shadow-lg' : 'bg-gradient-to-r from-gray-400 to-gray-500 text-gray-800'
          ]"
                style="min-height: 44px;">
          <LogIn v-if="!isLoading" :size="18" class="sm:w-5 sm:h-5"/>
          <svg v-if="isLoading"
               class="w-5 h-5 animate-spin"
               fill="none"
               viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
          </svg>
          <span>
            {{ isLoading ? 'Вход...' : 'Зарегистрироваться' }}
          </span>
        </button>
      </form>
    </LiquidGlass>
  </div>
  <Toaster :duration="5000" :expand="true" :visible-toasts="15"/>
</template>

<style scoped>
.safe-area-inset {
  padding-top: env(safe-area-inset-top);
  padding-bottom: env(safe-area-inset-bottom);
  padding-left: env(safe-area-inset-left);
  padding-right: env(safe-area-inset-right);
}

/* Better touch targets for mobile */
@media (max-width: 640px) {
  * {
    -webkit-tap-highlight-color: transparent;
  }

  input {
    font-size: 16px; /* Prevent zoom on iOS */
  }

  button {
    min-height: 44px;
  }
}

/* Animation for focus state */
input:focus {
  animation: input-focus 0.3s ease;
}

@keyframes input-focus {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.005);
  }
  100% {
    transform: scale(1);
  }
}

/* Loading animation */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Responsive adjustments */
@media (max-width: 480px) {
  .liquid-glass {
    border-radius: 1rem;
    padding: 1.5rem;
  }

  h1 {
    font-size: 1.5rem;
  }

  input {
    padding: 0.75rem 1rem 0.75rem 2.5rem;
  }
}
</style>