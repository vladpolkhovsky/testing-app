<template>
  <LiquidGlass class="p-3 w-full">
    <TransitionGroup tag="div" class="grid grid-cols-1 w-full space-y-3 p-3">
      <div
        :key="item.user.userId"
        v-for="(item, index) in state.items"
        class="ms-8 me-8 flex justify-between items-center border-2 pt-3 pb-2 pe-5 rounded-2xl hover:scale-105 transition-all duration-200"
        :class="[ 'hover:bg-blue-700', {
            'bg-gradient-to-r from-red-700/80 scale-103 to-red-300/50': index == 0,
            'bg-gradient-to-r from-orange-500/70 scale-102 to-orange-200/50': index == 1,
            'bg-gradient-to-r from-green-300/70 scale-101 to-green-200/50': index == 2,
            'bg-gradient-to-r from-blue-300/70 to-white/50': index > 2
           }]"
      >
        <div class="flex justify-between items-center">
          <div class="w-[45px] text-end me-3 text-3xl montserrat-name">
            {{ index + 1 }}.
          </div>
          <div class="text-3xl montserrat-name">{{ item.user.username }}</div>
        </div>
        <TransitionGroup tag="div" class="flex justify-between items-center" name="list">
          <div key="rating" class="text-3xl michroma-regular">{{ formatNumber(item.rating) }}</div>
          <div key="diff" class="text-3xl michroma-regular ms-3 text-green-700 min-w-[120px]" v-if="item.diff > 0">+ {{ formatNumber(item.diff) }}</div>
        </TransitionGroup>
      </div>
    </TransitionGroup>
  </LiquidGlass>
</template>

<script lang="ts" setup>
import LiquidGlass from "@/component/LiquidGlass.vue";
import type { User } from "@/model/User";
import gsap from "gsap";
import { onMounted, reactive } from "vue";

const props = defineProps<{
  rating: Record<string, number>;
  participants: User[];
}>();

interface UserRating {
  user: User;
  rating: number;
  diff: number;
}

const state = reactive<{
  items: UserRating[];
}>({ items: [] });

const applyChanges = (newRating: Record<string, number>) => {
    console.log('apply change to rating', newRating);
    
    let maxDelay = 0;
    state.items.forEach((item, index) => {
        const diff = (newRating[item.user.userId] ?? item.rating) - item.rating;
        const delay = 300 + 150 * index;
        maxDelay = Math.max(maxDelay, delay + 1200);
        setTimeout(() => item.diff = diff, delay);
        if (diff > 0) setTimeout(() => gsap.to(item, {
            rating: item.rating + diff,
            diff: 0,
            duration: 2 
        }), delay + 1200);
    });

    setTimeout(() => stateSort(), maxDelay + 5000);
};

const stateSort = () => {
    state.items.sort((a, b) => -(a.rating - b.rating));
};

(window as any).randomize = () => {
  let c = JSON.parse(JSON.stringify(state)) as {
    items: UserRating[];
  };
  c.items.forEach((i) => {
    i.rating = i.rating + Math.floor(1000 * Math.random());
  });
  applyChanges(
    c.items.reduce((acc, arr) => {
      acc[arr.user.userId] = arr.rating;
      return acc;
    }, {} as Record<string, number>)
  );
};

defineExpose({
  applyChanges,
});

onMounted(() => {
  state.items = props.participants.map<UserRating>((p) => {
    const rating = props.rating[p.userId] ?? 0;
    return {
      diff: 0,
      rating: rating,
      user: p,
    };
  });
  stateSort();
});

const formatNumber = (value: number, showSign = false) => {
  const absValue = Math.abs(value)
  const formatted = absValue.toLocaleString('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  })

  if (value > 0 && showSign) return `+${ formatted }`
  if (value < 0) return `-${ formatted }`

  return formatted
}
</script>

<style>
    .list-move, /* apply transition to moving elements */
.list-enter-active,
.list-leave-active {
  transition: all 0.7s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(60px);
}

.list-leave-active {
  position: absolute;
}
</style>