package io.pelle.todo.controller;

import io.pelle.todo.TodoRepository;
import io.pelle.todo.dto.NewTodo;
import io.pelle.todo.dto.Todo;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@RequestMapping(path = "/api/todos")
@EnableWebMvc
public class TodoController {

  @Autowired private TodoRepository todoStorage;

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<Iterable<Todo>> list() {
    return new ResponseEntity<>(todoStorage.findAll(), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Todo> create(@Valid @RequestBody NewTodo newTodo) {
    Todo todo = todoStorage.save(new Todo(newTodo.getDescription()));
    return new ResponseEntity<>(todo, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    todoStorage.delete(id);
  }

  @RequestMapping(value = "/{id}/markCompleted", method = RequestMethod.PUT)
  public ResponseEntity<Todo> markCompleted(@PathVariable("id") UUID id) {
    Optional<Todo> todo = todoStorage.findById(id);

    if (todo.isPresent()) {
      todo.get().setComplete(true);
      return new ResponseEntity(todoStorage.save(todo.get()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/{id}/updateDescription", method = RequestMethod.PUT)
  public ResponseEntity<Todo> updateDescription(
      @PathVariable("id") UUID id, @RequestBody String description) {

    Optional<Todo> todo = todoStorage.findById(id);

    if (todo.isPresent()) {
      todo.get().setDescription(description);
      return new ResponseEntity<>(todoStorage.save(todo.get()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
