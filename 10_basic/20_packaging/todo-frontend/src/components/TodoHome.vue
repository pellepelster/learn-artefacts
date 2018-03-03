<template>
  <div class="container" style="width: 60%;">

    <transition name="fade">
      <div class="alert alert-danger" role="alert" v-for="error in errors">
        {{ error }}
      </div>
    </transition>

    <div class="row">
      <div class="col">
        <h1 class="text-center">New Todolist</h1>
      </div>
    </div>
    <div class="row alert alert-secondary" role="alert">
      <div class="col-12">
        <input type="text" class="form-control" v-model="newTodoList" v-on:keyup.13="createNewTodoList">
      </div>
    </div>
  </div>
</template>

<script>
import {HTTP} from './http'

export default {
  name: 'TodoHome',
  data () {
    return {
      newTodoList: '',
      errors: []
    }
  },
  methods: {
    addError (error) {
      this.errors.push(error)
      setTimeout(() => {
        var index = this.errors.indexOf(error)
        this.errors.splice(index, 1)
      }, 3000)
    },
    createNewTodoList: function () {
      HTTP.post('todolists', {
        name: this.newTodoList
      })
      .then(response => {
        console.log(response.data.uuid)
        this.$router.push({name: 'TodoList', params: {uuid: response.data.uuid}})
      })
      .catch(error => {
        this.addError(error)
      })

      this.newTodo = ''
    }
  }
}
</script>

<style scoped>
</style>
