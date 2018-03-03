package io.pelle.todo.controller;

import io.pelle.todo.dto.NewTodoItem;
import io.pelle.todo.dto.TodoItemResponse;
import io.pelle.todo.entities.TodoItemRepository;
import io.pelle.todo.dto.NewTodoList;
import io.pelle.todo.entities.TodoItem;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;

import io.pelle.todo.entities.TodoList;
import io.pelle.todo.dto.TodoListResponse;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api/todolists")
@EnableWebMvc
public class TodoListsController {

  @Autowired
  private TodoListRepository todoListRepository;

  @RequestMapping(path = "{listId}", method = RequestMethod.GET)
  public ResponseEntity<TodoListResponse> getTodoList(@PathVariable("listId") UUID listId) {

    Optional<TodoList> todoList = todoListRepository.findById(listId);

    if (todoList.isPresent()) {

      TodoListResponse response = new TodoListResponse(todoList.get());
      addTodoListSelfLink(todoList.get().getId(), response);
      TodoItemsController.addAddCreateItemLink(todoList.get().getId(), response);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    return ResponseEntity.notFound().build();
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<TodoListResponse> createTodoList(@Valid @RequestBody NewTodoList newTodoList) {

    TodoList todoList = todoListRepository.save(new TodoList(newTodoList.getName()));

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(todoList.getId()).toUri();

    TodoListResponse response = new TodoListResponse(todoList);

    addTodoListSelfLink(todoList.getId(), response);
    TodoItemsController.addAddCreateItemLink(todoList.getId(), response);

    return ResponseEntity.created(location).body(response);
  }

  private void addTodoListSelfLink(UUID uuid, ResourceSupport response) {
    ResponseEntity<?> self = methodOn(TodoListsController.class).getTodoList(uuid);
    Link link = linkTo(self).withRel(Link.REL_SELF);
    response.add(link);
  }

}
