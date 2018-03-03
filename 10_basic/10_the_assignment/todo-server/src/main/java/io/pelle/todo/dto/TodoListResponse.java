package io.pelle.todo.dto;

import io.pelle.todo.controller.TodoItemsController;
import io.pelle.todo.entities.TodoList;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TodoListResponse extends ResourceSupport {

  private UUID uuid;

  private String name;

  private final List<TodoItemResponse> items;

  public TodoListResponse(TodoList todoList) {
    this.items = todoList.getTodos().stream().map(todoItem -> {
      TodoItemResponse response = new TodoItemResponse(todoItem);
      TodoItemsController.addTodoItemDeleteLink(todoItem.getId(), response);
      return response;
    }).collect(Collectors.toList());
    this.uuid = todoList.getId();
    this.name = todoList.getName();
  }

  public List<TodoItemResponse> getItems() {
    return items;
  }

  public String getName() {
    return name;
  }

  public UUID getUuid() {
    return uuid;
  }
}
