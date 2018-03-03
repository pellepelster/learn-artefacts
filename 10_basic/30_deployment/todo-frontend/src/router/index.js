import Vue from 'vue'
import Router from 'vue-router'
import TodoList from '@/components/TodoList'
import TodoHome from '@/components/TodoHome'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/todolists/:uuid',
      name: 'TodoList',
      component: TodoList
    },
    {
      path: '/',
      name: 'TodoHome',
      component: TodoHome
    }
  ]
})
