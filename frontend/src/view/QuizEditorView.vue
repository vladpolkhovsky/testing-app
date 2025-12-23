<template xmlns="http://www.w3.org/1999/html">
  <div class="p-6 w-full max-w-6xl mx-auto text-2xl">
    <!-- Заголовок квиза -->
    <div class="mb-8">
      <label
          class="w-full block text-center text-2xl font-medium text-black mb-2 p-3 border rounded border-yellow-500 bg-yellow-50 mb-5"
          v-if="quiz.isDirty">
        Квиз не сохранён!
      </label>
      <label class="block text-xl font-medium text-gray-700 mb-2">
        Название квиза
      </label>
      <label class="block text-sm font-medium text-red-700 mb-2" v-if="quiz.hasErrorInTitle">
        Название квиза не может быть пустым
      </label>
      <label class="block text-sm font-medium text-red-700 mb-2" v-if="quiz.isTitleDirty">
        Название квиза не сохранено
      </label>
      <input v-model="quiz.title"
             @change="validateQuiz"
             @input="markQuizTitleDirty"
             type="text"
             class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
             placeholder="Введите название квиза"
      />
    </div>

    <!-- Список вопросов -->
    <div v-for="(question, questionIndex) in quiz.questions"
         :key="question.viewId"
         :class="['mb-6 p-4 border rounded-lg transition-colors',
                  question.isDirty ? 'border-yellow-500 bg-yellow-50' : 'border-gray-200']">

      <!-- Заголовок вопроса -->
      <div class="flex justify-between items-center mb-4">
        <div class="inline-flex items-center gap-3">
          <h3 class="text-2xl font-medium">Вопрос</h3>
          <input class="min-w-16 field-sizing-content" :placeholder="(questionIndex + 1).toString()" type="number"
                 step="1" min="1"
                 @change="moveQuestionToPosition($event, question)"/>
        </div>
        <CircleX @click="removeQuestion(questionIndex)" class="hover:text-red-500" :size="32"/>
      </div>

      <div class="flex justify-between items-center mb-4 w-full gap-3">
        <!-- Текст вопроса -->
        <div class="w-full">
          <label class="block text-xl w-full font-medium text-gray-700 mb-2">
            Текст вопроса
          </label>
          <label class="block text-sm w-full font-medium text-red-700 mb-2" v-if="question.hasErrorInText">
            Вопрос не может быть пустым
          </label>
          <textarea
              v-model="question.text"
              @change="markQuestionDirty(question)"
              @input="validateQuestion(question)"
              rows="3"
              class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Введите текст вопроса"
          />
        </div>

        <!-- Текст вопроса -->
        <div class="w-full">
          <label class="block text-xl w-full font-medium text-gray-700 mb-2">
            Объяснение ответа (Опционально)
          </label>
          <label class="block text-sm w-full font-medium text-red-700 mb-2" v-if="question.hasErrorInTextAlternative">
            Объяснение ответа либо пустое либо соджержит символы отличные от пробела
          </label>
          <textarea
              v-model="question.textAlternative"
              @change="markQuestionDirty(question)"
              @input="validateQuestion(question)"
              rows="3"
              class="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Введите текст объяснение ответа"
          />
        </div>
      </div>

      <div class="flex justify-between items-baseline mb-4 w-full gap-5">
        <div class="w-full p-2 border rounded">
          <div class="flex justify-between items-center mb-1">
            <div class="text-sm font-medium text-gray-700">Файл вопроса</div>
            <CircleX class="hover:text-red-500" :size="24" @click="removeImage(question)"/>
          </div>
          <img v-if="question.imageId" :src="'/api/quiz/image/' + question.imageId" class="w-full mb-2">
          <input type="file" @change="handleFileUpload($event, question, id => question.imageId = id)"
                 class="w-full text-sm text-black file:mr-4 file:py-2 file:px-4 file:rounded-md file:border file:text-sm file:font-semibold hover:file:bg-blue-400"/>
        </div>
        <div class="w-full p-2 border rounded">
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
        <div class="flex justify-between items-center mb-4">
          <label class="block font-medium text-gray-700 text-2xl">Ответы</label>
          <button @click="addAnswer(question)" class="text-blue-600 hover:text-blue-800 text-sm font-medium">+ Добавить
            ответ
          </button>
        </div>

        <div v-for="(answer, answerIndex) in question.answers" :key="answer.viewId" :class="[ 'flex flex-col gap-3 mb-2', {
          'p-3 border rounded-xl border-red-700': answer.textValidationError,
        }]">
          <label class="text-sm w-full font-medium text-red-700 mb-2" v-if="answer.textValidationError">Пустой
            ответ</label>
          <div class="flex items-center gap-3">
            <div class="relative inline-flex items-center w-12 h-12">
              <Transition name="slide-up">
                <CircleCheckBig v-if="answer.isCorrect" :size="32" class="text-green-500 absolute"/>
                <Circle v-else :size="32" class="text-red-500 absolute"
                        @click="questionUpdateCorrect(question, answerIndex)"/>
              </Transition>
            </div>
            <input v-model="answer.optionText"
                   @change="markQuestionDirty(question)"
                   @input="validateQuestion(question)"
                   type="text"
                   class="flex-1 text-xl p-2 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                   placeholder="Введите вариант ответа"/>
            <CircleX class="hover:text-red-500 ml-3" :size="32" @click="removeAnswer(questionIndex, answerIndex)"/>
          </div>
        </div>
      </div>

      <!-- Кнопка сохранения вопроса -->
      <div v-if="question.isDirty" class="flex justify-end text-xl font-medium text-red-600">
        Вопрос не сохранён.
      </div>
    </div>

    <!-- Кнопки управления -->
    <div class="flex justify-between gap-4 mt-8 mb-8">
      <button @click="addQuestion" class="text-blue-600 hover:text-blue-800 text-xl font-medium">+ Добавить вопрос
      </button>
      <div class="flex flex-col items-start gap-3">
        <div v-if="isNotSavableQuiz(quiz)" class="flex justify-end text-xl font-medium text-red-600">
          Квиз содержит ошибки.
        </div>
        <button @click="saveAllQuestions"
                v-if="quiz.isDirty"
                :disabled="savingAll || isNotSavableQuiz(quiz)"
                :class="['w-full p-2 border rounded-xl transition-all duration-100 font-medium', {
                  'bg-green-100 hover:bg-green-600': quiz.isDirty && !isNotSavableQuiz(quiz),
                  'bg-gray-200': isNotSavableQuiz(quiz)
                }]">
          {{ savingAll ? 'Сохранение...' : 'Сохранить' }}
        </button>
      </div>
    </div>

    <label
        class="w-full block text-center text-2xl font-medium text-black mb-2 p-3 border rounded border-yellow-500 bg-yellow-50"
        v-if="quiz.isDirty">
      Квиз не сохранён!
    </label>
  </div>
</template>

<script setup lang="ts">
import {CircleX, CircleCheckBig, Circle} from 'lucide-vue-next';
import {ref, reactive, computed, onMounted} from 'vue'
import {
  type Answer, type AnswerEditor,
  isNotSavableQuiz, type Question, type QuestionEditor, type Quiz, type QuizEditor
} from "@/model/QuizTypes.ts"
import * as z from "zod"
import {useRoute} from "vue-router";

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
        text: 'Как создать перменную в Vue.js?',
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
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(quiz)
  }).then<Quiz>(res => res.json())
      .then<QuizEditor>(quiz => mapQuizToQuizEditor(quiz))
      .then(value => {
        Object.assign(quiz, value);
      });
}

// Функции для загрузки данных с бэкенда
const loadQuizFromBackend = async () => {
  fetch(`/api/quiz/editor/${route.params.quizId}`)
      .then<Quiz>(res => res.json())
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
    isTitleDirty: false
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