package io.pelle.todo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InMemoryTodoStorage implements TodoStorage {

  private List<Todo> todos = new ArrayList<>();

  @Override
  public List<Todo> allTodos() {
    return todos;
  }

  @Override
  public Optional<Todo> getTodoById(UUID uuid) {
    return todos.stream().filter(t -> t.getUuid().equals(uuid)).findFirst();
  }

  @Override
  public Todo create(Todo todo) {
    todos.add(todo);
    return todo;
  }

  @Override
  public void deleteTodo(UUID uuid) {
    Optional<Todo> todo = getTodoById(uuid);
    if (todo.isPresent()) {
      todos.remove(todo.get());
    }
  }

  @Override
  public Optional<Boolean> markCompleted(UUID uuid) {
    Optional<Todo> todo = getTodoById(uuid);
    if (todo.isPresent()) {
      todo.get().setComplete(true);
      return Optional.of(todo.get().isComplete());
    }

    return Optional.empty();
  }

  @Override
  public Optional<String> updateDescription(UUID uuid, String description) {
    Optional<Todo> todo = getTodoById(uuid);
    if (todo.isPresent()) {
      todo.get().setDescription(description);
      return Optional.of(todo.get().getDescription());
    }

    return Optional.empty();
  }
}
