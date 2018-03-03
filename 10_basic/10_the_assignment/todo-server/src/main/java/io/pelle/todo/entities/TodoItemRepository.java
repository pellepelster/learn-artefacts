package io.pelle.todo.entities;

import io.pelle.todo.entities.TodoItem;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, UUID> {
  Optional<TodoItem> findById(UUID id);
}
