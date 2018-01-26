package io.pelle.todo;

import io.pelle.todo.dto.Todo;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

  Optional<Todo> findById(UUID id);
}
