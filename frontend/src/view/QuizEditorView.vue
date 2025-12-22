<template xmlns="http://www.w3.org/1999/html">
  <div class="p-6 w-full max-w-6xl mx-auto text-2xl">
    <!-- Заголовок квиза -->
    <div class="mb-8">
      <label class="block text-xl font-medium text-red-700 mb-2" v-if="quiz.isDirty">
        Квиз не сохранён!
      </label>
      <label class="block text-xl font-medium text-gray-700 mb-2">
        Название квиза
      </label>
      <input
          v-model="quiz.title"
          @input="markQuizDirty"
          type="text"
          class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          placeholder="Введите название квиза"
      />
    </div>

    <!-- Список вопросов -->
    <div v-for="(question, questionIndex) in quiz.questions"
         :key="question.id"
         :class="['mb-6 p-4 border rounded-lg transition-colors',
                  question.isDirty ? 'border-yellow-500 bg-yellow-50' : 'border-gray-200']">

      <!-- Заголовок вопроса -->
      <div class="flex justify-between items-center mb-4">
        <div class="inline-flex items-center gap-3">
          <h3 class="text-2xl font-medium">Вопрос</h3>
          <input class="min-w-16 field-sizing-content" :placeholder="questionIndex + 1" type="number" step="1" min="1"
                 @change="moveQuestionToPosition($event, question)"/>
        </div>

        <CircleX @click="removeQuestion(questionIndex)" class="hover:text-red-500" :size="32"/>
      </div>

      <div class="flex justify-between items-center mb-4 w-full gap-3">
        <!-- Текст вопроса -->
        <div class="w-full">
          <label class="block text-sm w-full font-medium text-gray-700 mb-2">
            Текст вопроса
          </label>
          <textarea
              v-model="question.text"
              @input="markQuestionDirty(question)"
              rows="3"
              class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Введите текст вопроса"
          />
        </div>

        <!-- Текст вопроса -->
        <div class="w-full">
          <label class="block text-sm w-full font-medium text-gray-700 mb-2">
            Объяснение ответа (Опционально)
          </label>
          <textarea
              v-model="question.textAlternative"
              @input="markQuestionDirty(question)"
              rows="3"
              class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Введите текст объяснение ответа"
          />
        </div>
      </div>

      <div class="flex justify-between items-center mb-4 w-full gap-3">
        <div class="w-full">
          <div class="flex justify-between items-center mb-1">
            <div class="text-sm font-medium text-gray-700">Файл вопроса</div>
            <CircleX class="hover:text-red-500" :size="24" @click="removeImage(question)"/>
          </div>
          <img v-if="question.imageId" :src="'/api/quiz/image/' + question.imageId" class="w-full mb-2">
          <input type="file" @change="handleFileUpload($event, question, id => question.imageId = id)"
                 class="w-full text-sm text-black file:mr-4 file:py-2 file:px-4 file:rounded-md file:border file:text-sm file:font-semibold hover:file:bg-blue-400"/>
        </div>
        <div class="w-full">
          <div class="flex flex-row justify-between items-center mb-1">
            <label class="text-sm font-medium text-gray-700">Файл объяснение ответа</label>
            <CircleX class="hover:text-red-500" :size="24" @click="removeImageAlternative(question)"/>
          </div>
          <img v-if="question.imageAlternativeId" :src="'/api/quiz/image/' + question.imageAlternativeId"
               class="w-full mb-2">
          <input type="file" @change="handleFileUpload($event, question, id => question.imageAlternativeId = id)"
                 class="block w-full text-sm text-black file:mr-4 file:py-2 file:px-4 file:rounded-md file:border file:text-sm file:font-semibold hover:file:bg-blue-400"/>
        </div>
      </div>

      <!-- Ответы -->
      <div class="mb-4">
        <div class="flex justify-between items-center mb-2">
          <label class="block font-medium text-gray-700 text-2xl">Ответы</label>
          <button @click="addAnswer(question)" class="text-blue-600 hover:text-blue-800 text-sm font-medium">+ Добавить
            ответ
          </button>
        </div>

        <div v-for="(answer, answerIndex) in question.answers" :key="answerIndex" class="flex items-center mb-2">
          <div class="relative inline-flex items-center w-12 h-12">
            <Transition name="slide-up">
              <CircleCheckBig v-if="answer.isCorrect" :size="32" class="text-green-500 absolute"/>
              <Circle v-else :size="32" class="text-red-500 absolute"
                      @click="questionUpdateCorrect(question, answerIndex)"/>
            </Transition>
          </div>
          <input v-model="answer.text" @input="markQuestionDirty(question)" type="text"
                 class="flex-1 text-xl p-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                 placeholder="Введите вариант ответа"/>
          <CircleX class="hover:text-red-500 ml-3" :size="32" @click="removeAnswer(questionIndex, answerIndex)"/>
        </div>
      </div>

      <!-- Кнопка сохранения вопроса -->
      <div v-if="question.isDirty" class="flex justify-end text-xl font-medium">
        <button @click="saveQuestion(question)"
                class="p-2 border rounded-xl bg-green-100 hover:bg-green-600 transition-all duration-100">
          Сохранить
        </button>
      </div>
    </div>

    <!-- Кнопки управления -->
    <div class="flex justify-between gap-4 mt-8">
      <button @click="addQuestion" class="text-blue-600 hover:text-blue-800 text-xl font-medium">+ Добавить вопрос
      </button>
      <button v-if="hasDirtyQuestions"
              @click="saveAllQuestions"
              :disabled="savingAll"
              class="p-2 border rounded-xl bg-green-100 hover:bg-green-600 transition-all duration-100 font-medium"
      >
        {{ savingAll ? 'Сохранение...' : 'Сохранить всё' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {CircleX, CircleCheckBig, Circle} from 'lucide-vue-next';
import {ref, reactive, computed, onMounted} from 'vue'
import type {Answer, Question, QuestionEditor, Quiz, QuizEditor} from "@/model/QuizTypes.ts"
import * as z from "zod"

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
    id: null,
    title: '',
    questions: [],
    isDirty: false
  }
}

// Состояние квиза
const quiz = reactive<QuizEditor>(initialQuizState())
const savingAll = ref(false)

// Вычисляемые свойства
const hasDirtyQuestions = computed(() => {
  return quiz.questions.some(q => q.isDirty) || quiz.isDirty
})

// Методы
const markQuizDirty = () => {
  quiz.isDirty = true
}

const markQuestionDirty = (question: QuestionEditor) => {
  question.isDirty = true
  quiz.isDirty = true
}

const addQuestion = () => {
  const newQuestion = {
    id: null,
    text: '',
    order: generateId(),
    textAlternative: null,
    imageId: null,
    imageAlternativeId: null,
    answers: [
      {
        text: "Вариант",
        isCorrect: true,
        id: null
      }
    ],
    isDirty: true
  } as QuestionEditor;

  quiz.questions.push(newQuestion)
  quiz.isDirty = true
}

const removeQuestion = (index: number) => {
  quiz.questions.splice(index, 1)
  quiz.isDirty = true
}

const addAnswer = (question: QuestionEditor) => {
  question.answers.push({
    text: '',
    isCorrect: false,
    id: null
  });
  markQuestionDirty(question)
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
  const fromIndex = quiz.questions.findIndex(q => q.id == question.id);

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

const saveQuestion = async (question: QuestionEditor) => {
  console.log('Сохранение вопроса:', question.id, question);
}

const saveAllQuestions = async () => {
}

const resetToInitialState = () => {
  Object.assign(quiz, initialQuizState())
}

const validate = (quiz: Quiz) => {

}

// Функции для загрузки данных с бэкенда
const loadQuizFromBackend = async () => {
  // Здесь будет реальный запрос к API
  // Пример данных с сервера:
  const mockData = {
    title: 'Мой первый квиз',
    questions: [
      {
        id: generateId(),
        text: 'Что такое Vue.js?',
        fileName: 'vue-intro.pdf',
        fileId: 'file_123',
        answers: [{
          text: 'Фреймворк',
          isCorrect: false,
          id: generateId()
        }, {
          text: 'Библиотека',
          isCorrect: true,
          id: generateId()
        }],
        isDirty: false
      },
      {
        id: generateId(),
        text: 'Какой метод используется для создания реактивных данных в Composition API?',
        fileName: '',
        fileId: null,
        answers: [{
          id: null,
          text: "ref()",
          isCorrect: true
        }],
        isDirty: false
      }
    ]
  }

  quiz.title = mockData.title
  quiz.questions = mockData.questions.map(q => ({
    ...q,
    isDirty: false
  }))
  quiz.isDirty = false
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