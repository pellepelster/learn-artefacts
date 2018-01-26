package io.pelle.todo;

import io.pelle.todo.dto.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoStorage extends JpaRepository<Todo, String> {}
