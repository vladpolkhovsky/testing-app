<template>
  <div v-if="secondLeft != undefined"
       class="w-[300px] md:w-[450px] lg:w-[600px] xl:w-[800px] flex w-full items-center justify-center gap-5">
    <div class="w-full flex justify-end">
      <div class="bg-gradient-to-r text-transparent from-blue-600 to-purple-600 h-[5px]" :style="{'width': `${width.value}%`}"></div>
    </div>
    <div class="relative flex items-center justify-center h-[30px] w-[75px]">
      <Transition name="slide-up">
        <span :key="secondLeft"
              class="absolute text-xl md:text-3xl font-bold bg-gradient-to-r bg-clip-text text-transparent from-blue-600 to-purple-600">
          {{ secondLeft }}
        </span>
      </Transition>
    </div>
    <div class="w-full flex justify-start">
      <div class="bg-gradient-to-l text-transparent from-blue-600 to-purple-600 h-[5px]" :style="{'width': `${width.value}%`}"></div>
    </div>
  </div>

</template>

<script setup lang="ts">
import {computed, onMounted, onUnmounted, reactive, ref} from "vue";
import {gsap} from "gsap";

interface Props {
  color?: string;
  size?: number;
  seconds?: number;
}

const props = withDefaults(defineProps<Props>(), {
  color: "black",
  size: 38
});

let timerId: any = undefined;
const secondLeft = ref<number>();
const width = reactive<{ value: number }>({value: 100});

const rotationAngle = computed(() => {
  if (secondLeft.value === undefined) return 0;
  return secondLeft.value * 360 / 6;
});

// Прозрачность изменяется с каждой секундой
const clockOpacity = computed(() => {
  if (secondLeft.value === undefined) return 1;
  const progressInCycle = (secondLeft.value % 12) / 12;
  return 0.7 + 0.3 * Math.sin(progressInCycle * Math.PI);
});

onMounted(() => {
  timerId = setInterval(handleMinusSecond, 1000);
  secondLeft.value = props.seconds;
  startResize(props.seconds ?? 0)
});

onUnmounted(() => {
  clearInterval(timerId);
});

const handleMinusSecond = (): void => {
  //console.log("timer", secondLeft.value);
  if (secondLeft.value !== undefined && secondLeft.value > 0) {
    secondLeft.value = secondLeft.value - 1; 
  }
}

const updateTimer = (seconds: number): void => {
  secondLeft.value = seconds;
  startResize(seconds)
}

const hideTimer = (): void => {
  secondLeft.value = undefined;
}

const startResize = (seconds: number): void => {
  gsap.to(width, {
    value: 0,
    ease: "none",
    duration: seconds
  });
}

defineExpose({
  updateTimer,
  hideTimer
});
</script>

<style scoped>
.inset-0 {
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.25s ease-out;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

</style>