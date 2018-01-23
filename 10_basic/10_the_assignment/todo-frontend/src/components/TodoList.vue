<template>
  <div>
    <todo-item v-bind:message="item.message" v-for="item in items"></todo-item>
  </div>
</template>

<script>
import TodoItem from '@/components/TodoItem'

export default {
  name: 'TodoList',
  components: {
    'todo-item': TodoItem
  },
  data () {
    return { items: [
      { message: 'Foo' },
      { message: 'Bar' }
    ]
    }
  }
}
</script>

<style scoped>
</style>
