package io.pelle.todo.test;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  private String getUrl() {
    return getUrl("");
  }

  private String getUrl(String path) {
    return String.format("http://localhost:%d/api/todolists/%s", port, path);
  }

  @Test
  public void testCreateListAndAddTodoItem() throws Exception {

    ResponseEntity<String> result =
        this.restTemplate.postForEntity(
            getUrl(),
            RequestEntity.post(new URI(getUrl()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("{ \"name\": \"todolist 1\" }"),
            String.class);

    String todoListLocation = result.getHeaders().get("Location").get(0);

    String getTodoListResult =
        this.restTemplate.getForObject(
            todoListLocation,
            String.class,
            RequestEntity.get(new URI(getUrl())).accept(MediaType.APPLICATION_JSON).build());

    assertThat(JsonPath.read(getTodoListResult, "$.name"), is("todolist 1"));
    String createItemLink = JsonPath.read(getTodoListResult, "$._links.createitem.href");

    this.restTemplate.postForEntity(
        createItemLink,
        RequestEntity.post(new URI(getUrl()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body("{ \"description\": \"item 1\" }"),
        String.class);

    this.restTemplate.postForEntity(
        createItemLink,
        RequestEntity.post(new URI(getUrl()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body("{ \"description\": \"item 2\" }"),
        String.class);

    getTodoListResult =
        this.restTemplate.getForObject(
            todoListLocation,
            String.class,
            RequestEntity.get(new URI(getUrl())).accept(MediaType.APPLICATION_JSON).build());
    assertThat(JsonPath.read(getTodoListResult, "$.items.length()"), is(2));

    String deleteLink = JsonPath.read(getTodoListResult, "$.items[0]._links.delete.href");
    this.restTemplate.delete(deleteLink);

    getTodoListResult =
        this.restTemplate.getForObject(
            todoListLocation,
            String.class,
            RequestEntity.get(new URI(getUrl())).accept(MediaType.APPLICATION_JSON).build());
    assertThat(JsonPath.read(getTodoListResult, "$.items.length()"), is(1));
  }
}
