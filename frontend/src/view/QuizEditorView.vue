<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-6xl mx-auto">
    <LiquidGlass class="w-full m-5 xm:w-3xl p-8 flex flex-col gap-8" v-if="!quiz.authProblems">
      <!-- Заголовок квиза -->
      <LiquidGlass class="p-6">
        <div
            v-if="quiz.isDirty"
            class="w-full mb-6 p-4 rounded-2xl border-2 border-amber-400 bg-gradient-to-r from-amber-50 to-yellow-50 shadow-lg"
        >
          <div class="flex items-center justify-center gap-3">
            <svg class="w-6 h-6 text-amber-600 animate-pulse" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                    d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
                    clip-rule="evenodd"/>
            </svg>
            <span class="text-xl font-bold text-amber-800">Квиз не сохранён!</span>
          </div>
        </div>

        <label
            class="block text-2xl font-bold mb-4 bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
          Название квиза
        </label>
        <input
            v-model="quiz.title"
            @change="validateQuiz"
            @input="markQuizTitleDirty"
            type="text"
            class="w-full p-4 border-2 border-gray-300 rounded-2xl shadow-sm focus:ring-4 focus:ring-blue-200/50 focus:border-blue-400 transition-all duration-300 text-lg hover:border-blue-300"
            placeholder="Введите название квиза"
        />
        <ValidationErrorComponent errorText="Название квиза не может быть пустым" v-if="quiz.hasErrorInTitle"/>
        <ValidationErrorComponent errorText="Название квиза не сохранено" v-if="quiz.isTitleDirty"/>
      </LiquidGlass>

      <!-- Список вопросов -->
      <LiquidGlass
          v-for="(question, questionIndex) in quiz.questions"
          :key="question.viewId"
          class="p-6 transition-all duration-500"
          :class="[
          'hover:shadow-xl',
          question.isDirty ? 'ring-2 ring-amber-400 ring-offset-2 bg-gradient-to-br from-amber-50/50 to-yellow-50/50' : ''
        ]"
      >

        <!-- Заголовок вопроса -->
        <div class="flex justify-between items-center mb-6">
          <div class="inline-flex items-center gap-4">
            <div class="relative">
              <div
                  class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white font-bold text-lg shadow-lg">
                {{ questionIndex + 1 }}
              </div>
              <div
                  class="absolute -inset-1 bg-gradient-to-r from-blue-500/20 to-purple-500/20 rounded-xl blur opacity-50"></div>
            </div>
            <h3 class="text-2xl font-bold bg-gradient-to-r from-gray-800 to-gray-600 bg-clip-text text-transparent">
              Вопрос
            </h3>
          </div>

          <div class="flex items-center gap-3">
            <input
                class="w-20 p-2 border-2 border-gray-300 rounded-xl text-center font-medium focus:ring-2 focus:ring-blue-200 focus:border-blue-400 transition-all"
                :placeholder="(questionIndex + 1).toString()"
                type="number"
                step="1"
                min="1"
                @change="moveQuestionToPosition($event, question)"
                @input="markQuestionDirty(question)"
            />
            <button
                @click="removeQuestion(questionIndex)"
                class="group p-2 rounded-xl transition-all duration-300 hover:bg-rose-50 hover:shadow-lg"
            >
              <CircleX
                  class="text-gray-500 group-hover:text-rose-600 group-hover:scale-110 transition-transform duration-300"
                  :size="28"/>
            </button>
          </div>
        </div>

        <div class="flex flex-col lg:flex-row gap-6 mb-6">
          <!-- Текст вопроса -->
          <div class="lg:w-1/2">
            <label class="text-xl font-semibold text-gray-800 mb-3 flex items-center gap-2">
              <svg class="w-5 h-5 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd"
                      d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-3a1 1 0 00-.867.5 1 1 0 11-1.731-1A3 3 0 0113 8a3.001 3.001 0 01-2 2.83V11a1 1 0 11-2 0v-1a1 1 0 011-1 1 1 0 100-2zm0 8a1 1 0 100-2 1 1 0 000 2z"
                      clip-rule="evenodd"/>
              </svg>
              Текст вопроса
            </label>
            <textarea
                v-model="question.text"
                @change="markQuestionDirty(question)"
                @input="validateQuestion(question)"
                rows="4"
                class="w-full p-4 border-2 border-gray-300 rounded-2xl shadow-sm focus:ring-4 focus:ring-blue-200/50 focus:border-blue-400 transition-all duration-300 resize-none hover:border-blue-300"
                placeholder="Введите текст вопроса"
            />
            <ValidationErrorComponent errorText="Текст вопроса не может быть пустым" v-if="question.hasErrorInText"/>
          </div>

          <!-- Текст объяснения -->
          <div class="lg:w-1/2">
            <label class="text-xl font-semibold text-gray-800 mb-3 flex items-center gap-2">
              <svg class="w-5 h-5 text-purple-500" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd"
                      d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                      clip-rule="evenodd"/>
              </svg>
              Объяснение ответа <span class="text-sm font-normal text-gray-500">(Опционально)</span>
            </label>
            <textarea
                v-model="question.textAlternative"
                @change="markQuestionDirty(question)"
                @input="validateQuestion(question)"
                rows="4"
                class="w-full p-4 border-2 border-gray-300 rounded-2xl shadow-sm focus:ring-4 focus:ring-purple-200/50 focus:border-purple-400 transition-all duration-300 resize-none hover:border-purple-300"
                placeholder="Введите текст объяснения ответа"
            />
            <ValidationErrorComponent
                errorText="Объяснение ответа либо пустое либо содержит символы отличные от пробела"
                v-if="question.hasErrorInTextAlternative"
            />
          </div>
        </div>

        <div class="flex flex-col lg:flex-row gap-6 mb-6">
          <!-- Файл вопроса -->
          <div class="lg:w-1/2">
            <div
                class="border-2 border-gray-300 rounded-2xl p-4 transition-all duration-300 hover:border-blue-400 hover:shadow-lg">
              <div class="flex justify-between items-center mb-3">
                <label class="text-lg font-semibold text-gray-800 flex items-center gap-2">
                  <svg class="w-5 h-5 text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd"
                          d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z"
                          clip-rule="evenodd"/>
                  </svg>
                  Файл вопроса
                </label>
                <button
                    v-if="question.imageId"
                    @click="removeImage(question)"
                    class="p-1 rounded-lg hover:bg-rose-50 transition-colors"
                >
                  <CircleX class="text-gray-500 hover:text-rose-600" :size="20"/>
                </button>
              </div>

              <div v-if="question.imageId" class="mb-4">
                <div class="relative overflow-hidden rounded-xl group">
                  <img
                      :src="'/api/quiz/image/' + question.imageId"
                      class="w-full h-48 object-cover rounded-xl transition-transform duration-500 group-hover:scale-105"
                  />
                  <div
                      class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                </div>
              </div>

              <label class="block cursor-pointer">
                <div
                    class="flex items-center justify-center gap-2 p-4 border-2 border-dashed border-gray-300 rounded-xl hover:border-blue-400 hover:bg-blue-50/50 transition-all duration-300 group">
                  <svg class="w-6 h-6 text-gray-400 group-hover:text-blue-500" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd"
                          d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM6.293 6.707a1 1 0 010-1.414l3-3a1 1 0 011.414 0l3 3a1 1 0 01-1.414 1.414L11 5.414V13a1 1 0 11-2 0V5.414L7.707 6.707a1 1 0 01-1.414-1.414z"
                          clip-rule="evenodd"/>
                  </svg>
                  <span class="text-gray-600 group-hover:text-blue-600 font-medium">Загрузить изображение</span>
                </div>
                <input
                    type="file"
                    @change="handleFileUpload($event, question, id => question.imageId = id)"
                    class="hidden"
                    accept="image/*"
                />
              </label>
            </div>
          </div>

          <!-- Файл объяснения ответа -->
          <div class="lg:w-1/2">
            <div
                class="border-2 border-gray-300 rounded-2xl p-4 transition-all duration-300 hover:border-purple-400 hover:shadow-lg">
              <div class="flex justify-between items-center mb-3">
                <label class="text-lg font-semibold text-gray-800 flex items-center gap-2">
                  <svg class="w-5 h-5 text-purple-500" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd"
                          d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                          clip-rule="evenodd"/>
                  </svg>
                  Файл объяснения
                </label>
                <button
                    v-if="question.imageAlternativeId"
                    @click="removeImageAlternative(question)"
                    class="p-1 rounded-lg hover:bg-rose-50 transition-colors"
                >
                  <CircleX class="text-gray-500 hover:text-rose-600" :size="20"/>
                </button>
              </div>

              <div v-if="question.imageAlternativeId" class="mb-4">
                <div class="relative overflow-hidden rounded-xl group">
                  <img
                      :src="'/api/quiz/image/' + question.imageAlternativeId"
                      class="w-full h-48 object-cover rounded-xl transition-transform duration-500 group-hover:scale-105"
                  />
                  <div
                      class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                </div>
              </div>

              <label class="block cursor-pointer">
                <div
                    class="flex items-center justify-center gap-2 p-4 border-2 border-dashed border-gray-300 rounded-xl hover:border-purple-400 hover:bg-purple-50/50 transition-all duration-300 group">
                  <svg class="w-6 h-6 text-gray-400 group-hover:text-purple-500" fill="currentColor"
                       viewBox="0 0 20 20">
                    <path fill-rule="evenodd"
                          d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM6.293 6.707a1 1 0 010-1.414l3-3a1 1 0 011.414 0l3 3a1 1 0 01-1.414 1.414L11 5.414V13a1 1 0 11-2 0V5.414L7.707 6.707a1 1 0 01-1.414-1.414z"
                          clip-rule="evenodd"/>
                  </svg>
                  <span class="text-gray-600 group-hover:text-purple-600 font-medium">Загрузить изображение</span>
                </div>
                <input
                    type="file"
                    @change="handleFileUpload($event, question, id => question.imageAlternativeId = id)"
                    class="hidden"
                    accept="image/*"
                />
              </label>
            </div>
          </div>
        </div>

        <!-- Ответы -->
        <div class="mb-6">
          <div class="flex justify-between items-center mb-6">
            <label class="block text-2xl font-bold text-gray-800">Ответы</label>
            <button
                @click="addAnswer(question)"
                class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-lg"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
              </svg>
              Добавить ответ
            </button>
          </div>

          <div class="space-y-4">
            <div
                v-for="(answer, answerIndex) in question.answers"
                :key="answer.viewId"
                :class="[
                'p-4 rounded-2xl border-2 transition-all duration-300',
                answer.textValidationError
                  ? 'border-rose-400'
                  : 'border-gray-200 hover:border-blue-300 hover:shadow-lg'
              ]"
            >
              <div class="flex items-center gap-4">
                <!-- Кнопка выбора правильного ответа -->
                <button
                    @click="questionUpdateCorrect(question, answerIndex)"
                    :class="[
                    'relative w-10 h-10 rounded-full border-2 transition-all duration-300 flex-shrink-0',
                    answer.isCorrect
                      ? 'border-green-500 bg-green-50 shadow-inner'
                      : 'border-gray-300 hover:border-rose-400 hover:bg-rose-50'
                  ]"
                >
                  <div
                      :class="[
                      'absolute inset-1 rounded-full transition-all duration-300',
                      answer.isCorrect
                        ? 'bg-gradient-to-r from-green-400 to-emerald-500 shadow-lg'
                        : 'bg-gray-300'
                    ]"
                  ></div>
                  <CircleCheckBig
                      v-if="answer.isCorrect"
                      :size="16"
                      class="absolute inset-0 m-auto text-white z-10"
                  />
                </button>

                <!-- Поле ввода ответа -->
                <input
                    v-model="answer.optionText"
                    @change="markQuestionDirty(question)"
                    @input="validateQuestion(question)"
                    type="text"
                    :class="[
                    'flex-1 p-3 text-lg border-2 rounded-xl focus:ring-4 focus:ring-blue-200/50 transition-all duration-300',
                    answer.textValidationError
                      ? 'border-rose-400 focus:border-rose-500 focus:ring-rose-200/50'
                      : 'border-gray-300 focus:border-blue-400'
                  ]"
                    placeholder="Введите вариант ответа"
                />

                <!-- Кнопка удаления -->
                <button
                    @click="removeAnswer(questionIndex, answerIndex)"
                    :class="[
                    'p-2 rounded-lg transition-all duration-300 flex-shrink-0',
                    question.answers.length > 1
                      ? 'hover:bg-rose-50 hover:text-rose-600'
                      : 'opacity-50 cursor-not-allowed'
                  ]"
                    :disabled="question.answers.length <= 1"
                >
                  <CircleX :size="24"/>
                </button>
              </div>

              <ValidationErrorComponent
                  v-if="answer.textValidationError"
                  errorText="Ответ не может быть пустым"
                  class="mt-2"
              />
            </div>
          </div>
        </div>

        <!-- Уведомление о несохранённом вопросе -->
        <div
            v-if="question.isDirty"
            class="flex items-center justify-end gap-2 p-3 rounded-xl bg-gradient-to-r from-amber-50/80 to-yellow-50/80 border border-amber-200"
        >
          <svg class="w-5 h-5 text-amber-600 animate-pulse" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd"
                  d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                  clip-rule="evenodd"/>
          </svg>
          <span class="text-amber-700 font-medium">Вопрос не сохранён</span>
        </div>
      </LiquidGlass>

      <!-- Кнопки управления -->
      <LiquidGlass class="p-6">
        <div class="flex flex-col lg:flex-row justify-between items-center gap-6">
          <button
              @click="addQuestion"
              class="flex items-center gap-3 px-6 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-bold text-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-xl w-full lg:w-auto justify-center"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
            </svg>
            Добавить вопрос
          </button>

          <div class="flex flex-col items-end gap-3 w-full lg:w-auto">
            <ValidationErrorComponent
                v-if="isNotSavableQuiz(quiz)"
                errorText="Квиз содержит ошибки"
                class="text-lg"
            />

            <button
                @click="saveAllQuestions"
                v-if="quiz.isDirty"
                :disabled="savingAll || isNotSavableQuiz(quiz)"
                :class="[
                'px-8 py-4 rounded-2xl font-bold text-lg transition-all duration-300 w-full lg:w-auto',
                'disabled:opacity-50 disabled:cursor-not-allowed',
                !isNotSavableQuiz(quiz)
                  ? 'bg-gradient-to-r from-green-500 to-emerald-600 text-white hover:from-green-600 hover:to-emerald-700 hover:shadow-xl'
                  : 'bg-gradient-to-r from-gray-400 to-gray-500 text-gray-800'
              ]"
            >
              <span class="flex items-center justify-center gap-2">
                <svg
                    v-if="savingAll"
                    class="w-5 h-5 animate-spin"
                    fill="none"
                    viewBox="0 0 24 24"
                >
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                </svg>
                {{ savingAll ? 'Сохранение...' : 'Сохранить квиз' }}
              </span>
            </button>
          </div>
        </div>

        <!-- Уведомление о несохранённом квизе -->
        <div
            v-if="quiz.isDirty"
            class="mt-6 p-4 rounded-2xl border-2 border-amber-400 bg-gradient-to-r from-amber-50 to-yellow-50 shadow-lg"
        >
          <div class="flex items-center justify-center gap-3">
            <svg class="w-6 h-6 text-amber-600 animate-bounce" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd"
                    d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                    clip-rule="evenodd"/>
            </svg>
            <span class="text-xl font-bold text-amber-800">Не забудьте сохранить квиз!</span>
          </div>
        </div>
      </LiquidGlass>
    </LiquidGlass>
    <LiquidGlass v-else class="w-full m-5 xm:w-3xl p-8 flex flex-col gap-8">
      <LiquidGlass class="p-3">
        <div class="flex justify-between w-full items-center">
          <h1 class="inline text-2xl font-medium m-3">У вас нет доступа к данному квизу</h1>
          <div class="flex justify-end">
            <a href="/login-user"
               class="flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-lg group/play">
              <LogIn :size="20" class="group-hover/play:scale-110 transition-transform duration-300"/>
              <span>Войти</span>
            </a>
          </div>
        </div>
      </LiquidGlass>
    </LiquidGlass>
    <Toaster/>
  </div>
</template>

<script setup lang="ts">
import {CircleX, CircleCheckBig, Circle, LogIn} from 'lucide-vue-next';
import {ref, reactive, computed, onMounted} from 'vue'
import {
  type Answer, type AnswerEditor,
  isNotSavableQuiz, type Question, type QuestionEditor, type Quiz, type QuizEditor
} from "@/model/QuizTypes.ts"
import * as z from "zod"
import {useRoute} from "vue-router";
import LiquidGlass from "@/component/LiquidGlass.vue";
import ValidationErrorComponent from '@/component/ValidationErrorComponent.vue'
import {Toaster, toast} from "vue-sonner";

const route = useRoute();

// Генератор уникальных ID
let idCounter = 0

function generateId() {
  return idCounter++;
}

function generateStrId() {
  return (idCounter++).toString();
}

// Начальное состояние квиза
const initialQuizState = (): QuizEditor => {
  return {
    authProblems: false,
    id: null,
    title: 'Мой первый квиз',
    hasErrorInTitle: false,
    isDirty: true,
    isTitleDirty: true,
    questions: [
      {
        text: 'Что такое Vue.js?',
        viewId: generateStrId(),
        textAlternative: null,
        hasErrorInText: false,
        hasErrorInTextAlternative: false,
        hasNoCorrectAnswer: false,
        imageId: null,
        imageAlternativeId: null,
        answers: [{
          optionText: 'Фреймворк',
          viewId: generateStrId(),
          textValidationError: false,
          isCorrect: true
        }, {
          optionText: 'Библиотека',
          viewId: generateStrId(),
          textValidationError: false,
          isCorrect: false
        }],
        isDirty: true
      },
      {
        text: 'Как создать переменную в Vue.js?',
        viewId: generateStrId(),
        textAlternative: null,
        hasErrorInText: false,
        hasErrorInTextAlternative: false,
        hasNoCorrectAnswer: false,
        imageId: null,
        imageAlternativeId: null,
        answers: [{
          optionText: 'ref()',
          viewId: generateStrId(),
          textValidationError: false,
          isCorrect: true,
        }, {
          optionText: 'useRef()',
          viewId: generateStrId(),
          textValidationError: false,
          isCorrect: false
        }],
        isDirty: true
      }
    ]
  }
}

// Состояние квиза
const quiz = reactive<QuizEditor>(initialQuizState())
const savingAll = ref(false)

// Вычисляемые свойства
const hasDirtyQuestions = computed(() => {
  return quiz.questions.some(q => q.isDirty) || quiz.isDirty
})

const markQuizTitleDirty = () => {
  quiz.isTitleDirty = true;
  quiz.isDirty = true
}

const markQuestionDirty = (question: QuestionEditor) => {
  question.isDirty = true
  quiz.isDirty = true
}

const addQuestion = () => {
  const newQuestion = {
    text: 'Текст вопроса',
    textAlternative: null,
    imageId: null,
    imageAlternativeId: null,
    hasNoCorrectAnswer: false,
    hasErrorInTextAlternative: false,
    hasErrorInText: false,
    answers: [
      {
        optionText: "Вариант",
        isCorrect: true,
        viewId: generateStrId(),
        textValidationError: false
      }
    ],
    isDirty: true
  } as QuestionEditor;

  validateQuestion(newQuestion);

  quiz.questions.push(newQuestion)
  quiz.isDirty = true
}

const removeQuestion = (index: number) => {
  quiz.questions.splice(index, 1);
  quiz.isDirty = true;
}

const addAnswer = (question: QuestionEditor) => {
  question.answers.push({
    optionText: 'Вариант ответа',
    textValidationError: false,
    isCorrect: false,
    viewId: generateStrId(),
  });
  markQuestionDirty(question);
}

const questionUpdateCorrect = (question: QuestionEditor, correctIndex: number) => {
  question.answers.forEach((value, index) => {
    value.isCorrect = index == correctIndex;
  });
  markQuestionDirty(question)
}

const removeAnswer = (questionIndex: number, answerIndex: number) => {
  const question = quiz.questions[questionIndex]
  if (question!.answers.length > 1) {
    question!.answers.splice(answerIndex, 1)
    if (!question!.answers.find(a => a.isCorrect)) question!.answers![0]!.isCorrect = true;
    markQuestionDirty(question as QuestionEditor)
  }
}

const handleFileUpload = async (event: Event, question: QuestionEditor, handler: (newId: string) => void) => {
  const file = (event.target as HTMLInputElement)?.files?.[0]!

  const form = new FormData();
  form.append("image", file);

  console.log(form.entries());

  let response = await fetch(`/api/quiz/image`, {
    method: 'POST',
    body: form
  });

  const json = await response.json() as { id: string };

  handler(json.id);

  markQuestionDirty(question)
}

function moveElement(arr: any[], fromIndex: number, toIndex: number) {
  // Make sure the indices are within the array bounds
  if (fromIndex < 0 || fromIndex >= arr.length || toIndex < 0 || toIndex >= arr.length) {
    console.error("Indices are out of bounds.");
    return arr; // Return the original array unchanged
  }

  // 1. Remove the element from the original position
  // splice(startIndex, deleteCount) returns an array of the deleted elements
  const [elementToMove] = arr.splice(fromIndex, 1);

  // 2. Insert the removed element into the new position
  // splice(startIndex, deleteCount, item1, item2, ...) inserts elements
  arr.splice(toIndex, 0, elementToMove);

  return arr; // Return the modified array
}

const moveQuestionToPosition = (event: Event, question: QuestionEditor) => {
  console.log(event, ((event.target as HTMLInputElement).value))

  const toIndex = Math.max(Math.min(quiz.questions.length, parseInt((event.target as HTMLInputElement).value)) - 1, 0);
  const fromIndex = quiz.questions.findIndex(q => q.viewId == question.viewId);

  (event.target as HTMLInputElement).value = '';

  console.log(toIndex, fromIndex)

  moveElement(quiz.questions, fromIndex, toIndex);
}

const removeImage = (question: QuestionEditor) => {
  question.imageId = null;
  markQuestionDirty(question);
}

const removeImageAlternative = (question: QuestionEditor) => {
  question.imageAlternativeId = null;
  markQuestionDirty(question);
}

const validateQuestion = (question: QuestionEditor) => {
  const text = z.string().nonempty().safeParse(question.text)
  const textAlternative = z.union([
    z.null(),
    z.literal(""),
    z.string().nonempty()
  ]).safeParse(question.textAlternative);

  question.hasErrorInText = !text.success;
  question.hasErrorInTextAlternative = !textAlternative.success;

  question.answers.forEach(answer => {
    const text = z.string().nonempty().safeParse(answer.optionText)
    answer.textValidationError = !text.success;
  })

  question.hasNoCorrectAnswer = !(question.answers.filter(question => question.isCorrect).length == 1)
}

const validateQuiz = () => {
  const text = z.string().nonempty().safeParse(quiz.title)
  quiz.hasErrorInTitle = !text.success;
}

const saveQuestion = async (question: QuestionEditor) => {
  console.log('Сохранение вопроса:', question);
}

const saveAllQuestions = async () => {
  fetch(`/api/quiz/editor/${route.params.quizId}`, {
    method: 'POST',
    redirect: "manual",
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(quiz)
  }).then(res => {
    if (res.status != 200) {
      quiz.authProblems = true;
      toast.error("У вас нет доступа к квизу!")
      throw new Error();
    }
    return res;
  }).then<Quiz>(res => res.json())
      .then<QuizEditor>(quiz => mapQuizToQuizEditor(quiz))
      .then(value => {
        Object.assign(quiz, value);
      });
}

// Функции для загрузки данных с бэкенда
const loadQuizFromBackend = async () => {
  fetch(`/api/quiz/editor/${route.params.quizId}`, {
    redirect: "manual"
  }).then(res => {
    if (res.status != 200) {
      quiz.authProblems = true;
      toast.error("У вас нет доступа к квизу!")
      throw new Error();
    }
    return res;
  }).then<Quiz>(res => res.json())
      .then<QuizEditor>(quiz => mapQuizToQuizEditor(quiz))
      .then(value => {
        Object.assign(quiz, value);
      });
}

const mapQuizToQuizEditor = (quiz: Quiz): QuizEditor => {
  return ({
    ...quiz,
    questions: quiz.questions.map<QuestionEditor>(question => ({
      ...question,
      answers: question.answers.map<AnswerEditor>(answer => ({
        ...answer,
        viewId: generateStrId(),
        textValidationError: false
      })),
      viewId: generateStrId(),
      hasErrorInText: false,
      hasErrorInTextAlternative: false,
      hasNoCorrectAnswer: false,
      isDirty: false
    })),
    hasErrorInTitle: false,
    isDirty: false,
    isTitleDirty: false,
    authProblems: false
  })
}

// Хук жизненного цикла
onMounted(() => {
  loadQuizFromBackend()
})

// Экспортируем функции для тестирования и внешнего использования
defineExpose({})
</script>

<style>

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease-out;
}

.slide-up-enter-from {
  opacity: 0;
}

.slide-up-leave-to {
  opacity: 0;
}
</style>