package io.pelle.todo.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pelle.todo.controller.TodoListsController;
import io.pelle.todo.dto.NewTodoItem;
import io.pelle.todo.dto.NewTodoList;
import io.pelle.todo.entities.TodoItem;
import io.pelle.todo.entities.TodoItemRepository;
import io.pelle.todo.entities.TodoList;
import io.pelle.todo.entities.TodoListRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoListsController.class)
@WebAppConfiguration
public class TodoListsControllerTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @MockBean private TodoListRepository todoListRepository;

  @Autowired private MockMvc mvc;

  @Test
  public void getTodoListWithInvalidUUID() throws Exception {
    when(todoListRepository.findById(any())).thenReturn(Optional.empty());

    mvc.perform(get("/api/todolist/{uuid}", UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void getTodoList() throws Exception {
    UUID uuid = UUID.randomUUID();

    TodoList list = new TodoList();
    list.getTodos().add(new TodoItem(uuid,"xxx", false));

    when(todoListRepository.findById(any())).thenReturn(Optional.of(list));

    mvc.perform(get("/api/todolists/{uuid}", UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].uuid", is(uuid.toString())))
        .andExpect(jsonPath("$.items[0].description", is("xxx")))
        .andExpect(jsonPath("$.items[0].complete", is(false)));
//        .andExpect(jsonPath("$.items[0]._links.delete.href", is(String.format("http://localhost/api/todolists/%s", uuid))));
  }

  @Test
  public void testCreateTodoList() throws Exception {

    final NewTodoList newTodoList = new NewTodoList("todolist1");

    UUID uuid = UUID.randomUUID();
    final TodoList todoList = new TodoList(uuid,"todolist1");
    when(todoListRepository.save(any(TodoList.class))).thenReturn(todoList);

    mvc.perform(
        post("/api/todolists")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(newTodoList)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.uuid", is(uuid.toString())))
        .andExpect(
            header()
                .string(
                    "Location", String.format("http://localhost/api/todolists/%s", uuid.toString())));

    verify(todoListRepository, times(1)).save(Mockito.any(TodoList.class));
    verifyNoMoreInteractions(todoListRepository);
  }

}
