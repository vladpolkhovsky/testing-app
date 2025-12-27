<script setup lang="ts">
import { inject, onMounted, ref } from 'vue'
import { getRandomRating, type RatingItem } from "@/model/RatingItem.ts"
import { SocketService } from "@/service/SocketService.ts";
import {CirclePlay} from "lucide-vue-next"
import gsap from "gsap";

interface RankedItem {
  id: string
  index: number
  position: number
  diff: number
  item: RatingItem
  oldRank?: number
}

const props = defineProps<{
  items?: RatingItem[]
}>()

const rankedItems = ref<RankedItem[]>([]);
const socketService = inject<SocketService>("SocketService");

const initializeItems = () => {
  rankedItems.value = [...(props.items ?? [])]
      .sort((a, b) => b.rating - a.rating)
      .map((item, index) => ({
        id: item.userId,
        index: index + 1,
        position: index,
        diff: 0,
        item
      }));
}

const updateModel = (newItems: RatingItem[]) => {
  console.log('new items', newItems);

  newItems.forEach((item) => {
    const find = rankedItems.value.find(value => value.item.userId === item.userId);
    console.log('already found in rating ', find);
    if (!find) {
      rankedItems.value.push({
        diff: 0,
        index: rankedItems.value.length + 1,
        position: rankedItems.value.length,
        id: item.userId,
        item: item,
        oldRank: item.rating
      })
    }
  })

  console.log('updated rating items', rankedItems.value);

  let hasAnyDiff = false;

  const updated = rankedItems.value.map(ranked => {
    const newItem = newItems.find(i => i.userId === ranked.id)

    if (newItem) {
      const diff = newItem.rating - ranked.item.rating

      if (diff != 0) {
        hasAnyDiff = true;

        gsap.to(ranked, {
          diff: diff,
          duration: 5,
          ease: "power4.inOut"
        }).then(() => {
          gsap.to(ranked.item, {
            rating: ranked.item.rating + diff,
            delay: 2,
            duration: 3,
            ease: "power4.inOut"
          });
          gsap.to(ranked, {
            diff: 0,
            delay: 2,
            duration: 3,
            ease: "power4.inOut"
          });
        });
      }
      return ranked;
    }

    return ranked;
  });

  const update = () => {
    updated.sort((a, b) => b.item.rating - a.item.rating);

    updated.forEach((item, index) => {
      item.index = index + 1
      item.position = index
    });

    rankedItems.value = updated;
    emits("round-update-ended");
  }

  if (hasAnyDiff) {
    setTimeout(update, 12_000);
    return;
  }

  update();
}

(window as any).getRandomRating = () => {
  updateModel(getRandomRating())
}

const addUser = (item: RatingItem) => {
  if (rankedItems.value.find(r => r.id === item.userId)) {
    return;
  }

  const newRankedItem: RankedItem = {
    id: item.userId,
    index: rankedItems.value.length + 1,
    position: rankedItems.value.length,
    diff: 0,
    item
  }

  rankedItems.value.push(newRankedItem)

  const newItems = [...rankedItems.value.map(r => r.item), item]
  updateModel(newItems)
}

const formatNumber = (value: number, showSign = false) => {
  const absValue = Math.abs(value)
  const formatted = absValue.toLocaleString('en-US', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 1
  })

  if (value > 0 && showSign) return `+${ formatted }`
  if (value < 0) return `-${ formatted }`

  return formatted
}

const showStartGameButtonRef = ref<boolean>(false)

const showStartGameButton = () => {
  showStartGameButtonRef.value = true;
}

const onShowStartGameButton = () => {
  showStartGameButtonRef.value = false;
  socketService?.startQuiz();
}

const clearUsers = () => {
  rankedItems.value = []
}

defineExpose({
  updateModel,
  addUser,
  showStartGameButton,
  clearUsers,
});

const emits = defineEmits(["round-update-ended"]);

onMounted(() => {
  initializeItems()
});

</script>

<template>
  <div ref="container" class="rating-container rounded-xl w-full">
    <div class="flex w-full justify-between items-center p-3" v-if="showStartGameButtonRef">
      <div class="text-center text-3xl montserrat-person">
        Ожидание игроков
      </div>
      <button
          class="flex items-center gap-3 text-2xl px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-lg"
          v-if="rankedItems.length > 0"
          @click="onShowStartGameButton()">
        <CirclePlay /> Начать игру
      </button>
    </div>
    <TransitionGroup name="list" tag="div" class="space-y-3">
      <div v-for="item in rankedItems"
           :key="item.id"
           :id="`item-${item.id}`"
           class="rating-item"
           :class="[ 'hover:bg-green-500', {
            'bg-gradient-to-r from-sky-700/80 to-green-300/50': item.index == 1,
            'bg-gradient-to-r from-sky-500/70 to-green-200/50': item.index == 2,
            'bg-gradient-to-r from-sky-300/70 to-green-200/50': item.index == 3,
            'bg-gradient-to-r from-gray-300/70 to-white/50': item.index > 3
           }]"
           :style="{transition: 'all 0.3s eazy', zIndex: 100 - item.position}">

        <div class="flex items-center justify-between px-5 py-3">
          <div class="flex items-center space-x-4">
            <div class="flex flex-col items-center">
              <span class="w-8 text-2xl font-bold text-gray-800 tektur-badge">{{ item.index }}.</span>
            </div>
            <div class="flex items-center space-x-3">
              <div>
                <h3 class="text-2xl font-semibold text-gray-900 montserrat-person">{{ item.item.username }}</h3>
              </div>
            </div>
          </div>

          <div class="flex gap-10 justify-end items-center">
            <div class="flex items-center space-x-6">
              <div class="text-right">
                <div class="text-2xl font-bold text-gray-900">
                  <span class="iconify tektur-badge" data-inline="false">{{ formatNumber(item.item.rating) }}</span>
                </div>
                <div class="text-md text-gray-500 tektur-badge">Рейтинг</div>
              </div>
            </div>
            <div class="flex items-center space-x-6" v-if="item.diff != 0">
              <div class="text-start">
                <div :class="[ 'text-2xl font-bold tektur-badge', {
                    'text-green-900': item.diff > 0,
                    'text-red-900': item.diff < 0
                    }]">
                  {{ formatNumber(item.diff) }}
                </div>
                <div class="text-sm text-gray-500">Результат раунда</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&family=Pangolin&family=Tektur:wght@400..900&display=swap');

.tektur-badge {
  font-family: "Tektur", sans-serif;
  font-optical-sizing: auto;
  font-weight: bold;
  font-variation-settings: "wdth" 100;
}

.pangolin-regular {
  font-family: "Pangolin", cursive;
  font-weight: 400;
  font-style: normal;
}

.qr-position {
  top: 25px;
  right: 25px;
}

@reference "@/main.css"

.rating-container {
  @apply relative min-h-[400px];
}

.rating-item {
  @apply relative rounded-xl shadow-sm border border-gray-200
  transition-all duration-700 ease-in-out overflow-hidden;
}

.montserrat-person {
  font-family: "Montserrat", sans-serif;
  font-optical-sizing: auto;
  font-weight: bold;
  font-style: normal;
}
</style>