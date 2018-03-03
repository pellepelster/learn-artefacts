package io.pelle.todo.controller;

import io.pelle.todo.dto.NewTodoItem;
import io.pelle.todo.dto.TodoItemResponse;
import io.pelle.todo.entities.TodoItem;
import io.pelle.todo.entities.TodoItemRepository;
import io.pelle.todo.entities.TodoList;
import io.pelle.todo.entities.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api/todoitems")
@EnableWebMvc
public class TodoItemsController {

  @Autowired private TodoItemRepository todoItemRepository;

  @Autowired private TodoListRepository todoListRepository;

  @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@PathVariable("id") UUID id) {

    Optional<TodoItem> todoItem = todoItemRepository.findById(id);
    if (!todoItem.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    todoItem.get().getTodoList().getTodos().remove(todoItem.get());
    todoItemRepository.delete(id);

    return ResponseEntity.noContent().build();
  }

  @RequestMapping(value = "{id}/markCompleted", method = RequestMethod.PUT)
  public ResponseEntity<TodoItem> markCompleted(@PathVariable("id") UUID id) {
    Optional<TodoItem> todo = todoItemRepository.findById(id);

    if (todo.isPresent()) {
      todo.get().setComplete(true);
      return new ResponseEntity(todoItemRepository.save(todo.get()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "{id}/updateDescription", method = RequestMethod.PUT)
  public ResponseEntity<TodoItem> updateDescription(
      @PathVariable("id") UUID id, @RequestBody String description) {

    Optional<TodoItem> todo = todoItemRepository.findById(id);

    if (todo.isPresent()) {
      todo.get().setDescription(description);
      return new ResponseEntity<>(todoItemRepository.save(todo.get()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(path = "{listId}", method = RequestMethod.POST)
  public ResponseEntity<TodoItemResponse> createTodoItem(@PathVariable("listId") UUID listId, @Valid @RequestBody NewTodoItem newTodo) {

    Optional<TodoList> todoList = todoListRepository.findById(listId);

    if (!todoList.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    TodoItem todoItem = new TodoItem(newTodo.getDescription());
    todoItem.setTodoList(todoList.get());

    todoItem = todoItemRepository.save(todoItem);
    todoList.get().getTodos().add(todoItem);

    todoListRepository.save(todoList.get());

    TodoItemResponse response = new TodoItemResponse(todoItem);
    addTodoItemDeleteLink(todoItem.getId(), response);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  public static void addTodoItemDeleteLink(UUID uuid, ResourceSupport response) {
    ResponseEntity<?> self = methodOn(TodoItemsController.class).delete(uuid);
    Link link = linkTo(self).withRel("delete");
    response.add(link);
  }


  public static void addAddCreateItemLink(UUID uuid, ResourceSupport response) {
    ResponseEntity<?> self = methodOn(TodoItemsController.class).createTodoItem(uuid, null);
    Link link = linkTo(self).withRel("createitem");
    response.add(link);
  }


}
