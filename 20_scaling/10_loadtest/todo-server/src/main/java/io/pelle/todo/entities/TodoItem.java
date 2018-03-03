package io.pelle.todo.entities;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class TodoItem implements Serializable {

  @Id @Column private UUID id;

  @Column()
  private String description;

  @Column()
  private boolean complete;

  @ManyToOne(fetch = FetchType.EAGER)
  private TodoList todoList;

  public TodoItem() {}

  public TodoItem(UUID id, String description, boolean complete) {
    this.id = id;
    this.description = description;
    this.complete = complete;
  }

  public TodoItem(String description) {
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

  public TodoList getTodoList() {
    return todoList;
  }

  public void setTodoList(TodoList todoList) {
    this.todoList = todoList;
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
