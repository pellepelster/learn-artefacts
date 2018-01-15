package io.pelle.todo.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pelle.todo.Todo;
import io.pelle.todo.TodoApplication;
import io.pelle.todo.TodoController;
import io.pelle.todo.TodoStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TodoApplication.class)
public class TodoControllerTest {

  @MockBean
  private TodoStorage todoStorage;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private TodoController todoController;

  private MockMvc mvc;

  @Before
  public void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(todoController).build();
  }

  @Test
  public void testListTodos() throws Exception {
    UUID uuid = UUID.randomUUID();
		when(todoStorage.allTodos()).thenReturn(Arrays.asList(new Todo(uuid, "todo 1", false)));

    mvc.perform(get("/api/todos")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].uuid", is(uuid.toString())))
				.andExpect(jsonPath("$[0].description", is("todo 1")))
				.andExpect(jsonPath("$[0].complete", is(false)));

		 verify(todoStorage, times(1)).allTodos();
		 verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testCreateTodo() throws Exception {
    final Todo todo = new Todo("todo 2");
		ObjectMapper objectMapper = new ObjectMapper();
		final byte[] bytes = objectMapper.writeValueAsBytes(todo);

		when(todoStorage.create(Mockito.any(Todo.class))).thenReturn(todo);

		mvc.perform(post("/api/todos")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.content(bytes))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.uuid", not(isEmptyOrNullString())))
          .andExpect(jsonPath("$.description", is("todo 2")))
          .andExpect(jsonPath("$.complete", is(false)));

		verify(todoStorage, times(1)).create(Mockito.any(Todo.class));

    ArgumentCaptor<Todo> argument = ArgumentCaptor.forClass(Todo.class);
    verify(todoStorage).create(argument.capture());
    assertThat("todo 2", is(argument.getValue().getDescription()));

		verifyNoMoreInteractions(todoStorage);
	}

	@Test
	public void testDelete() throws Exception {
    UUID uuid = UUID.randomUUID();
		doThrow(new IllegalArgumentException()).when(todoStorage).deleteTodo(null);

		mvc.perform(delete("/api/todos/{uuid}", uuid.toString()))
				.andExpect(status().isNoContent());

    verify(todoStorage, times(1)).deleteTodo(eq(uuid));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testMarkCompleted() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.markCompleted(eq(uuid))).thenReturn(Optional.of(true));

    mvc.perform(put("/api/todos/{uuid}/markCompleted", uuid.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(true)));

    verify(todoStorage, times(1)).markCompleted(eq(uuid));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testMarkCompletedWithInvalidUUID() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.markCompleted(eq(uuid))).thenReturn(Optional.empty());

    mvc.perform(put("/api/todos/{uuid}/markCompleted", uuid.toString()))
        .andExpect(status().isNotFound());
  }


  @Test
  public void testUpdateDescription() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.updateDescription(eq(uuid), eq("todo 3"))).thenReturn(Optional.of("todo 3"));

    mvc.perform(put("/api/todos/{uuid}/updateDescription", uuid.toString())
        .content("todo 3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("todo 3")));


    verify(todoStorage, times(1)).updateDescription(eq(uuid), eq("todo 3"));
    verifyNoMoreInteractions(todoStorage);
  }

  @Test
  public void testUpdateDescriptionWithInvalidUUID() throws Exception {
    UUID uuid = UUID.randomUUID();

    when(todoStorage.updateDescription(eq(uuid), eq("todo 3"))).thenReturn(Optional.empty());

    mvc.perform(put("/api/todos/{uuid}/updateDescription", uuid.toString()).content("todo 3"))
        .andExpect(status().isNotFound());

    verify(todoStorage, times(1)).updateDescription(eq(uuid), eq("todo 3"));
    verifyNoMoreInteractions(todoStorage);
  }

}