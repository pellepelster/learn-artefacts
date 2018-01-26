package io.pelle.todo.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pelle.todo.TodoRepository;
import io.pelle.todo.controller.TodoController;
import io.pelle.todo.dto.Todo;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
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

@RunWith(SpringRunner.class)
@WebMvcTest(TodoController.class)
@WebAppConfiguration
public class TodoControllerTest {

  @MockBean private TodoRepository todoStorage;

  @Autowired private MockMvc mvc;

  @Test
  public void testListTodos() throws Exception {
    UUID uuid = UUID.randomUUID();
    when(todoStorage.findAll()).thenReturn(Arrays.asList(new Todo(uuid, "todo 1", false)));

    mvc.perform(get("/api/todos").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(uuid.toString())))
        .andExpect(jsonPath("$[0].description", is("todo 1")))
        .andExpect(jsonPath("$[0].complete", is(false)));

    verify(todoStorage, times(1)).findAll();
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testCreateTodo() throws Exception {
    final Todo todo = new Todo("todo 2");
    ObjectMapper objectMapper = new ObjectMapper();
    final byte[] bytes = objectMapper.writeValueAsBytes(todo);

    when(todoStorage.save(any(Todo.class))).thenReturn(todo);

    mvc.perform(
            post("/api/todos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bytes))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", not(isEmptyOrNullString())))
        .andExpect(jsonPath("$.description", is("todo 2")))
        .andExpect(jsonPath("$.complete", is(false)));

    verify(todoStorage, times(1)).save(Mockito.any(Todo.class));

    ArgumentCaptor<Todo> argument = ArgumentCaptor.forClass(Todo.class);
    verify(todoStorage).save(argument.capture());
    assertThat("todo 2", is(argument.getValue().getDescription()));

    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testCreateTodoWithEmptyDescription() throws Exception {
    final Todo todo = new Todo();
    ObjectMapper objectMapper = new ObjectMapper();
    final byte[] bytes = objectMapper.writeValueAsBytes(todo);

    mvc.perform(
            post("/api/todos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bytes))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest());

    verifyZeroInteractions(todoStorage);
  }

  @Test
  public void testDelete() throws Exception {
    UUID uuid = UUID.randomUUID();

    doNothing().when(todoStorage).delete(any(UUID.class));

    mvc.perform(delete("/api/todos/{id}", uuid.toString())).andExpect(status().isNoContent());

    verify(todoStorage, times(1)).delete(eq(uuid));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testMarkCompleted() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.findById(any(UUID.class)))
        .thenReturn(Optional.of(new Todo(uuid, "xxx", false)));
    when(todoStorage.save(any(Todo.class))).thenReturn(new Todo(uuid, "xxx", true));

    mvc.perform(put("/api/todos/{id}/markCompleted", uuid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.complete", is(true)));

    verify(todoStorage, times(1)).findById(eq(uuid));
    verify(todoStorage, times(1)).save(any(Todo.class));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testMarkCompletedWithInvalidUUID() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.findById(any(UUID.class))).thenReturn(Optional.empty());

    mvc.perform(put("/api/todos/{uuid}/markCompleted", uuid.toString()))
        .andExpect(status().isNotFound());

    verify(todoStorage, times(1)).findById(eq(uuid));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testUpdateDescription() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.findById(any(UUID.class)))
        .thenReturn(Optional.of(new Todo(uuid, "xxx", false)));
    when(todoStorage.save(any(Todo.class))).thenReturn(new Todo(uuid, "todo 3", true));

    mvc.perform(
            put("/api/todos/{id}/updateDescription", uuid.toString())
                .accept(MediaType.APPLICATION_JSON)
                .content("todo 3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description", is("todo 3")));

    verify(todoStorage, times(1)).findById(eq(uuid));
    verify(todoStorage, times(1)).save(any(Todo.class));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testUpdateDescriptionWithInvalidUUID() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.findById(any(UUID.class))).thenReturn(Optional.empty());

    mvc.perform(put("/api/todos/{id}/updateDescription", uuid.toString()).content("todo 3"))
        .andExpect(status().isNotFound());

    verify(todoStorage, times(1)).findById(eq(uuid));
    verifyNoMoreInteractions(todoStorage);
  }
}
