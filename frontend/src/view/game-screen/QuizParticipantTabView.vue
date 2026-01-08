<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import type { QuizContextDto } from "@/model/quiz/QuizContextDto";
import type { User } from "@/model/User";
import { useQRCode } from "@vueuse/integrations/useQRCode";
import { Delete, DeleteIcon, Loader, UserRoundMinus } from "lucide-vue-next";
import { toast } from "vue-sonner";

const inviteLink = window.location.toString() + "/p";
const props = defineProps<{ context: QuizContextDto }>();
const qrCode = useQRCode(inviteLink);

const deletePerson = (user: User) => {
  fetch(`/api/quiz/editor/context/delete/${props.context.id}/${user.userId}`, {
    method: "POST",
  }).then((resp) => {
    if (resp.status != 200) {
      throw new Error(resp.statusText)
    }
  }).then(() => {
    emit("removeParticipan", user);
  }).catch((error: Error) => {
    toast.error(error.message)
  });
};

const emit = defineEmits<{
  removeParticipan: [user: User];
}>();
</script>

<template>
  <LiquidGlass class="p-3">
    <div class="grid grid-cols-2 p-3">
      <div class="text-2xl mb-3 font-medium">
        <p class="mb-3">Участники</p>
        <TransitionGroup name="list" tag="ul" class="p-3">
          <li
            :key="p.userId"
            v-for="(p, index) in context.participants"
            class="flex justify-between items-center border-b pb-3 mb-3"
          >
            <div>{{ index + 1 }}. {{ p.username }}</div>
            <UserRoundMinus
              color="red"
              :size="32"
              class="hover:scale-110"
              @click="deletePerson(p)"
            />
          </li>
          <div
            v-if="!context.participants.length"
            class="text-xl flex gap-3 items-center"
          >
            <Loader :size="32" class="spinner" />
            <div>Ожидание подключения</div>
          </div>
        </TransitionGroup>
      </div>
      <div class="p-3 w-full">
        <p class="text-2xl text-center mb-3 font-medium">
          Для присоедниенения отсканируйте
        </p>
        <img :src="qrCode" class="mx-auto min-w-[300px] rounded-2xl mb-3" />
        <div class="mx-auto w-fit">
          <input
            class="inline-block border-3 border-dashed rounded-md p-3 text-xl michroma-regular field-sizing-content"
            readonly
            @focus="($event.target as HTMLInputElement).select()"
            type="text"
            :value="inviteLink"
          />
        </div>
      </div>
    </div>
  </LiquidGlass>
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

.spinner {
  animation: spin 2s ease-in-out infinite;
}
</style>
