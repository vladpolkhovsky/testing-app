<template>
    <div class="flex gap-3 w-full"
        :class="[orientation === 'vertical' ? 'flex-row justify-between' : 'flex-col']">
        <!-- Текст вопроса -->
        <div :class="[orientation === 'vertical' ? 'min-w-1/3' : 'w-full']">
            <h2 class="text-3xl font-semibold mb-4 text-gray-800">
                Вопрос:
            </h2>
            <p class="text-gray-700 whitespace-pre-wrap text-4xl font-medium">
                {{ question }}
            </p>
        </div>

        <!-- Изображение -->
        <div v-if="imageId" class="relative flex items-center justify-center"
            :class="[orientation === 'vertical' ? 'min-h-[500px] max-h-[75vh]' : 'max-h-150']">
            <img ref="imageRef" :src="`/api/quiz/image/${imageId}`" alt="Иллюстрация к вопросу" class="rounded-md border"
                :class="[orientation === 'vertical' ? 'min-h-[500px] max-h-[75vh]' : 'max-h-150']"
                @load="handleImageLoad" @error="handleImageError" />
            <div v-if="!imageLoaded" class="absolute inset-0 flex items-center justify-center">
                <div class="text-gray-400">
                    Загрузка изображения...
                </div>
            </div>
            <div v-if="imageError" class="absolute inset-0 flex items-center justify-center bg-red-50">
                <div class="text-red-500 text-center p-4">
                    Ошибка загрузки изображения
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Props {
    question: string
    imageId: string
}

const props = defineProps<Props>()

const imageRef = ref<HTMLImageElement | null>(null)
const orientation = ref<'vertical' | 'horizontal'>('vertical')
const imageLoaded = ref(false)
const imageError = ref(false)

const handleImageLoad = () => {
    imageLoaded.value = true
    imageError.value = false

    if (!imageRef.value) return

    const img = imageRef.value
    orientation.value = img.naturalHeight > img.naturalWidth ? 'vertical' : 'horizontal'
}

const handleImageError = () => {
    imageLoaded.value = false
    imageError.value = true
}

// Обработка изменения URL изображения
const observer = new MutationObserver(() => {
    if (imageRef.value?.complete) {
        handleImageLoad()
    }
})

onMounted(() => {
    if (imageRef.value) {
        observer.observe(imageRef.value, { attributes: true, attributeFilter: ['src'] })
    }
})

onUnmounted(() => {
    observer.disconnect()
})
</script>