<template>
  <div class="container" style="width: 60%;">

    <transition name="fade">
      <div class="alert alert-danger" role="alert" v-for="error in errors">
        {{ error }}
      </div>
    </transition>

    <div class="row">
      <div class="col">
        <h1 class="text-center">{{ listName }}</h1>
      </div>
    </div>
    <div class="row alert alert-secondary" role="alert">
      <div class="col-12">
        <input type="text" class="form-control" v-model="newTodo" v-on:keyup.13="addTodo">
      </div>
    </div>
    <div class="row alert alert-primary align-items-center" role="alert" v-for="(todo, index) in todos" :key="todo.uuid">
      <div class="col-11">
        <span v-show="!todo.edit" v-on:click="toggleEdit(index, todo)">{{todo.description}}</span>
        <input type="text" ref="inputs" class="form-control" v-model="todo.description" v-show="todo.edit" v-on:blur="saveEdit(index, todo)">
      </div>
      <div class="col-1">
        <button v-on:click="deleteTodo(todo)" type="button" class="btn btn-outline-danger">
          <i class="fa fa-trash-o" aria-hidden="true"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import {HTTP} from './http'
import Vue from 'vue'

export default {
  name: 'TodoList',
  data () {
    return {
      newTodo: '',
      listName: '<none>',
      todos: [],
      errors: []
    }
  },
  methods: {
    toggleEdit (index, todo) {
      todo.edit = !todo.edit

      if (todo.edit) {
        Vue.nextTick(() => {
          this.$refs.inputs[index].focus()
        })
      }
    },
    saveEdit (index, todo) {
      HTTP.put('todoitems/' + todo.uuid + '/updateDescription', this.$refs.inputs[index].value)
      .then(response => {
        this.toggleEdit(index, todo)
      })
      .catch(error => {
        this.addError(error)
      })
    },
    addError (error) {
      this.errors.push(error)
      setTimeout(() => {
        var index = this.errors.indexOf(error)
        this.errors.splice(index, 1)
      }, 3000)
    },
    updateList () {
      HTTP.get('todolists/' + this.$route.params.uuid).then(response => {
        this.todos = response.data.items.map((todo) => { return { 'uuid': todo.uuid, 'description': todo.description, 'edit': false } })
        this.listName = response.data.name
      })
      .catch(e => {
        this.errors.push(e)
      })
    },
    addTodo: function () {
      HTTP.post('todoitems/' + this.$route.params.uuid, {
        description: this.newTodo
      })
      .then(response => {
        this.updateList()
      })
      .catch(error => {
        this.addError(error)
      })

      this.newTodo = ''
    },
    deleteTodo: function (todo) {
      HTTP.delete('todoitems/' + todo.uuid).then(response => {
        var index = this.todos.indexOf(todo)
        this.todos.splice(index, 1)
      })
      .catch(error => {
        this.addError(error)
      })
    }
  },
  created () {
    this.updateList()
  }
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 1.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
</style>
