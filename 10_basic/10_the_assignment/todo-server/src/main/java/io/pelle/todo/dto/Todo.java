package io.pelle.todo.dto;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Todo implements Serializable {

  @Id @Column private UUID id;

  @Column()
  private String description;

  @Column()
  private boolean complete;

  public Todo() {}

  public Todo(UUID id, String description, boolean complete) {
    this.id = id;
    this.description = description;
    this.complete = complete;
  }

  public Todo(String description) {
    this(UUID.randomUUID(), description, false);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isComplete() {
    return complete;
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
  }

  @Override
  public String toString() {
    return "[ id="
        + this.id
        + ", description="
        + this.description
        + ", complete="
        + this.complete
        + " ]";
  }
}
