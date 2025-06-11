<template>
  <div class="star-rating">
    <div class="stars-display" v-if="readOnly">
      <i v-for="n in 5" 
         :key="n" 
         class="fas" 
         :class="n <= rating ? 'fa-star' : 'fa-star-o'"
         :style="{ color: n <= rating ? '#FFD700' : '#ccc' }"></i>
      <span v-if="showCount" class="rating-count">({{ count }})</span>
    </div>
    
    <div v-else class="stars-input">
      <i v-for="n in 5" 
         :key="n" 
         class="fas" 
         :class="n <= hover || n <= modelValue ? 'fa-star' : 'fa-star-o'"
         :style="{ color: n <= hover || n <= modelValue ? '#FFD700' : '#ccc' }"
         @mouseover="hover = n"
         @mouseleave="hover = 0"
         @click="$emit('update:modelValue', n)"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StarRating',
  props: {
    modelValue: {
      type: Number,
      default: 0
    },
    rating: {
      type: Number,
      default: 0
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    count: {
      type: Number,
      default: 0
    },
    showCount: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      hover: 0
    }
  }
}
</script>

<style scoped>
.star-rating {
  display: inline-flex;
  align-items: center;
}

.stars-display, .stars-input {
  display: flex;
  align-items: center;
}

.fas {
  cursor: pointer;
  font-size: 1.2rem;
  margin-right: 2px;
}

.stars-input .fas:hover {
  transform: scale(1.1);
}

.rating-count {
  margin-left: 5px;
  color: #666;
  font-size: 0.9rem;
}
</style>