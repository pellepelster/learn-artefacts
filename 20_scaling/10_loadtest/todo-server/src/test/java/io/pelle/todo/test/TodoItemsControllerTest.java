package io.pelle.todo.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pelle.todo.dto.NewTodoItem;
import io.pelle.todo.entities.TodoItemRepository;
import io.pelle.todo.entities.TodoListRepository;
import io.pelle.todo.controller.TodoListsController;
import io.pelle.todo.entities.TodoItem;

import java.util.Optional;
import java.util.UUID;

import io.pelle.todo.dto.NewTodoList;
import io.pelle.todo.entities.TodoList;
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
@WebMvcTest(TodoListsController.class)
@WebAppConfiguration
public class TodoItemsControllerTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @MockBean private TodoItemRepository todoItemRepository;

  @MockBean private TodoListRepository todoListRepository;

  @Autowired private MockMvc mvc;

  @Test
  public void testCreateTodo() throws Exception {

    UUID listUUID = UUID.randomUUID();
    UUID todoUUID = UUID.randomUUID();

    TodoList list = new TodoList(listUUID, "list 1");
    when(todoListRepository.findById(eq(listUUID))).thenReturn(Optional.of(list));

    TodoItem todoItem = new TodoItem(todoUUID,"todo 2", false);
    when(todoItemRepository.save(any(TodoItem.class))).thenReturn(todoItem);

    final NewTodoItem newTodoItem = new NewTodoItem("todo 2");
    mvc.perform(
            post("/api/todoitems/{listUUID}", listUUID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(newTodoItem)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.uuid", is(todoUUID.toString())))
        .andExpect(jsonPath("$.description", is("todo 2")))
        .andExpect(jsonPath("$.complete", is(false)))
        .andExpect(jsonPath("$._links.delete.href", is(String.format("http://localhost/api/todoitems/%s", todoItem.getId()))));

    verify(todoItemRepository, times(1)).save(Mockito.any(TodoItem.class));

    ArgumentCaptor<TodoItem> argument = ArgumentCaptor.forClass(TodoItem.class);
    verify(todoItemRepository).save(argument.capture());
    assertThat("todo 2", is(argument.getValue().getDescription()));

    verifyNoMoreInteractions(todoItemRepository);
  }

  @Test
  public void testCreateTodoWithEmptyDescription() throws Exception {

    UUID listUUID = UUID.randomUUID();
    UUID todoUUID = UUID.randomUUID();

    TodoList list = new TodoList(listUUID, "list 1");
    when(todoListRepository.findById(eq(listUUID))).thenReturn(Optional.of(list));

    TodoItem todoItem = new TodoItem(todoUUID,"todo 2", false);
    when(todoItemRepository.save(any(TodoItem.class))).thenReturn(todoItem);

    final NewTodoItem newTodoItem = new NewTodoItem();
    mvc.perform(
        post("/api/todoitems/{listUUID}", listUUID)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(newTodoItem)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testDelete() throws Exception {
    UUID uuid = UUID.randomUUID();

    TodoItem item = new TodoItem();
    item.setTodoList(new TodoList());
    when(todoItemRepository.findById(any())).thenReturn(Optional.of(item));
    doNothing().when(todoItemRepository).delete(any(UUID.class));

    mvc.perform(delete("/api/todoitems/{id}", uuid.toString())).andExpect(status().isNoContent());

    verify(todoItemRepository, times(1)).delete(eq(uuid));
  }

  @Test
  public void testMarkCompleted() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoItemRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(new TodoItem(uuid, "xxx", false)));
    when(todoItemRepository.save(any(TodoItem.class))).thenReturn(new TodoItem(uuid, "xxx", true));

    mvc.perform(put("/api/todoitems/{id}/markCompleted", uuid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.complete", is(true)));

    verify(todoItemRepository, times(1)).findById(eq(uuid));
    verify(todoItemRepository, times(1)).save(any(TodoItem.class));
    verifyNoMoreInteractions(todoItemRepository);
  }

  @Test
  public void testMarkCompletedWithInvalidUUID() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoItemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    mvc.perform(put("/api/todoitems/{uuid}/markCompleted", uuid.toString()))
        .andExpect(status().isNotFound());

    verify(todoItemRepository, times(1)).findById(eq(uuid));
    verifyNoMoreInteractions(todoItemRepository);
  }

  @Test
  public void testUpdateDescription() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoItemRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(new TodoItem(uuid, "xxx", false)));
    when(todoItemRepository.save(any(TodoItem.class))).thenReturn(new TodoItem(uuid, "todo 3", true));

    mvc.perform(
            put("/api/todoitems/{id}/updateDescription", uuid.toString())
                .accept(MediaType.APPLICATION_JSON)
                .content("todo 3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description", is("todo 3")));

    verify(todoItemRepository, times(1)).findById(eq(uuid));
    verify(todoItemRepository, times(1)).save(any(TodoItem.class));
    verifyNoMoreInteractions(todoItemRepository);
  }

  @Test
  public void testUpdateDescriptionWithInvalidUUID() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoItemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    mvc.perform(put("/api/todoitems/{id}/updateDescription", uuid.toString()).content("todo 3"))
        .andExpect(status().isNotFound());

    verify(todoItemRepository, times(1)).findById(eq(uuid));
    verifyNoMoreInteractions(todoItemRepository);
  }
}
