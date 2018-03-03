package io.pelle.todo.entities;

import io.pelle.todo.entities.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, UUID> {
  Optional<TodoList> findById(UUID id);
}
