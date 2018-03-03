package io.pelle.todo.dto;

import io.pelle.todo.entities.TodoItem;
import org.springframework.hateoas.ResourceSupport;

import java.util.UUID;

public class TodoItemResponse extends ResourceSupport {

  private String description;

  private UUID uuid;

  private boolean complete;

  public TodoItemResponse(TodoItem todoItem) {
    this.description = todoItem.getDescription();
    this.uuid = todoItem.getId();
    this.complete = todoItem.isComplete();
  }

  public String getDescription() {
    return description;
  }

  public UUID getUUID() {
    return uuid;
  }

  public boolean isComplete() {
    return complete;
  }
}
