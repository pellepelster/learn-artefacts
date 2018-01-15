package io.pelle.todo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoStorage {

  List<Todo> allTodos();

  Optional<Todo> getTodoById(UUID uuid);

  Todo create(Todo todo);

  void deleteTodo(UUID uuid);

  Optional<Boolean> markCompleted(UUID uuid);

  Optional<String> updateDescription(UUID uuid, String description);
}
