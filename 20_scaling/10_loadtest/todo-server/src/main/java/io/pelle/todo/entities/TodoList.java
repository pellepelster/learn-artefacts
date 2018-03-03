package io.pelle.todo.entities;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class TodoList implements Serializable {

  @Id @Column private UUID id;

  @Column()
  private String name;

  @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)
  private List<TodoItem> todos = new ArrayList<>();

  public TodoList() {}

  public TodoList(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public TodoList(String name) {
    this(UUID.randomUUID(), name);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TodoItem> getTodos() {
    return todos;
  }

  public void setTodos(List<TodoItem> todos) {
    this.todos = todos;
  }

  @Override
  public String toString() {
    return "[ id="
        + this.id
        + ", name="
        + this.name
        + " ]";
  }
}
