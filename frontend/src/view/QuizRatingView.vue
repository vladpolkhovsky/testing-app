<script setup lang="ts">
import {ref, onMounted, inject} from 'vue'
import {getRandomRating, type RatingItem} from "@/model/RatingItem.ts"
import LiquidGlass from "@/component/LiquidGlass.vue";
import {SocketService} from "@/service/SocketService.ts";

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

const container = ref<HTMLElement>();
const rankedItems = ref<RankedItem[]>([]);
const socketService = inject<SocketService>("SocketService");

// Инициализация
const initializeItems = () => {
  const sorted = [...(props.items ?? [])]
      .sort((a, b) => b.rating - a.rating)
      .map((item, index) => ({
        id: item.userId,
        index: index + 1,
        position: index,
        diff: 0,
        item
      }));

  rankedItems.value = sorted;
}

const updateModel = (newItems: RatingItem[]) => {
  const updated = rankedItems.value.map(ranked => {
    const newItem = newItems.find(i => i.userId === ranked.id)
    if (newItem) {
      const diff = newItem.rating - ranked.item.rating
      return {
        ...ranked,
        diff,
        item: {...ranked.item, rating: newItem.rating}
      }
    }
    return ranked
  })

  updated.sort((a, b) => b.item.rating - a.item.rating)

  updated.forEach((item, index) => {
    item.index = index + 1
    item.position = index
  })

  rankedItems.value = updated
}

window.updateModel = () => {
  updateModel(getRandomRating());
}

window.addUser = () => {
  addUser({...getRandomRating()[0], userId: new Date().toString(), username: new Date().toString()});
}

const addUser = (item: RatingItem) => {
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

  if (value > 0 && showSign) return `+${formatted}`
  if (value < 0) return `-${formatted}`
  return formatted
}

const getDiffColor = (diff: number) => {
  if (diff > 0) return 'text-emerald-600 bg-emerald-50 border-emerald-200'
  if (diff < 0) return 'text-rose-600 bg-rose-50 border-rose-200'
  return 'text-gray-600 bg-gray-50 border-gray-200'
}

const showStartGameButtonRef = ref<boolean>(true)

const showStartGameButton = () => {
  showStartGameButtonRef.value = true;
}

const onShowStartGameButton = () => {
  showStartGameButtonRef.value = false;
  socketService?.startGame();
}

defineExpose({
  updateModel,
  addUser,
  showStartGameButton
});

onMounted(() => {
  initializeItems()
});

</script>

<template>
  <LiquidGlass class="rounded-xl">
    <div ref="container" class="rating-container">
      <TransitionGroup name="list" tag="div" class="space-y-3">
        <div class="flex w-full justify-between items-center p-3" v-if="showStartGameButtonRef">
          <div class="text-center text-3xl">
            Ожидание участников
          </div>
          <button class="border bg-emerald-100 rounded-xl p-3 hover:bg-emerald-400 transition-all duration-300 text-2xl font-medium"
                  v-if="rankedItems.length > 0"
                  @click="onShowStartGameButton()">
            Начать игру
          </button>
        </div>
        <div v-for="item in rankedItems"
             :key="item.id"
             :id="`item-${item.id}`"
             class="rating-item"
             :class="[ 'hover:bg-gray-300', {
            'bg-gradient-to-r from-sky-700/80 to-green-300/50': item.index == 1,
            'bg-gradient-to-r from-sky-500/70 to-green-200/50': item.index == 2,
            'bg-gradient-to-r from-sky-300/70 to-green-200/50': item.index == 3,
            'bg-gradient-to-r from-gray-300/70 to-white/50': item.index > 3
           }]"
             :style="{transition: 'all 0.3s eazy', zIndex: 100 - item.position}">

          <div class="flex items-center justify-between px-5 py-3">
            <div class="flex items-center space-x-4">
              <div class="flex flex-col items-center">
                <span class="w-8 text-2xl font-bold text-gray-800">{{ item.index }}.</span>
              </div>
              <div class="flex items-center space-x-3">
                <div>
                  <h3 class="text-2xl font-semibold text-gray-900">{{ item.item.username }}</h3>
                </div>
              </div>
            </div>

            <div class="flex gap-10 justify-end items-center">
              <div class="flex items-center space-x-6">
                <div class="text-right">
                  <div class="text-2xl font-bold text-gray-900">
                    <span class="iconify" data-icon="material-symbols:arrow-shape-up-stack-2"
                          data-inline="false"></span> {{ formatNumber(item.item.rating) }}
                  </div>
                  <div class="text-sm text-gray-500">Рейтинг</div>
                </div>
              </div>
              <div class="flex items-center space-x-6" v-if="item.diff != 0">
                <div class="text-start">
                  <div class="text-2xl font-bold text-gray-900">{{ formatNumber(item.diff) }}</div>
                  <div class="text-sm text-gray-500">Результат раунда</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </TransitionGroup>
    </div>
  </LiquidGlass>
</template>

<style scoped>
@reference "@/main.css"

.rating-container {
  @apply relative min-h-[400px];
}

.rating-item {
  @apply relative rounded-xl shadow-sm border border-gray-200
  transition-all duration-700 ease-in-out overflow-hidden;
}
</style>