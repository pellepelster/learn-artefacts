package io.pelle.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("io.pelle.todo")
public class TodoApplication {
  public static void main(String[] args) {
    SpringApplication.run(TodoApplication.class, args);
  }
}
