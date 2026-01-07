<script lang="ts" setup>
import LiquidGlass from "@/component/LiquidGlass.vue";
import QuizAnswer from "@/component/QuizAnswer.vue";
import QuizQuestion from "@/component/QuizQuestion.vue";
import type { QuizQuestionDto } from "@/model/quiz/QuizQuestionDto";
import { reactive } from "vue";

const props = defineProps<{ question: QuizQuestionDto }>();

const currentQuestion = reactive<QuizQuestionDto>(props.question);

const showCorrectAnswer = () => {
  currentQuestion.text = props.question.textAlternative ?? props.question.text;
  currentQuestion.imageId = props.question.imageAlternativeId ?? props.question.imageId;
  currentQuestion.answers = props.question.answers.filter((t) => t.isCorrect);
};

defineExpose({
  showCorrectAnswer,
});

</script>

<template>
  <div class="grid grid-cols-3 gap-3 w-full h-full">
    <LiquidGlass class="col-span-2">
      <div class="flex flex-col items-start justify-baseline gap-5 p-3 h-full">
        <div class="relarive flex justify-around h-full w-full ps-3 pe-3">
          <QuizQuestion
            :question="currentQuestion.text"
            :image-id="currentQuestion.imageId"
          />
        </div>
      </div>
    </LiquidGlass>
    <LiquidGlass>
      <TransitionGroup name="list">
        <QuizAnswer :key="ans.id" v-for="ans in currentQuestion.answers" :answer="ans" />
      </TransitionGroup>
    </LiquidGlass>
  </div>
</template>

<style lang="css" scoped>
  .list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.7s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-300px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(300px);
}

.list-leave-active {
  position: absolute;
}
</style>
