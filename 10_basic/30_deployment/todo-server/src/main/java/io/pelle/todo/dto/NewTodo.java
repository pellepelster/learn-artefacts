package io.pelle.todo.dto;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class NewTodo implements Serializable {

  @NotNull @NotEmpty private String description;

  public NewTodo() {}

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("description", description).toString();
  }
}
