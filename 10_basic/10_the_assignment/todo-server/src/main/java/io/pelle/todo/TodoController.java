package io.pelle.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

	@Autowired
	private TodoStorage todoStorage;

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Todo>> list() {
		return new ResponseEntity<>(todoStorage.allTodos(), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Todo> create(@RequestBody Todo todoToCreate) {
		Todo todo = todoStorage.create(todoToCreate);
    return new ResponseEntity<>(todo, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("uuid") String uuid) {
		todoStorage.deleteTodo(UUID.fromString(uuid));
	}

	@RequestMapping(value = "/{uuid}/markCompleted", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> markCompleted(@PathVariable("uuid") String uuid) {

		Optional<Boolean> updatedComplete = todoStorage.markCompleted(UUID.fromString(uuid));

		if (updatedComplete.isPresent()) {
			return new ResponseEntity<>(updatedComplete.get(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@RequestMapping(value = "/{uuid}/updateDescription", method = RequestMethod.PUT)
	public ResponseEntity<String> updateDescription(@PathVariable("uuid") String uuid, @RequestBody String description) {
		Optional<String> updatedDescription = todoStorage.updateDescription(UUID.fromString(uuid), description);

		if (updatedDescription.isPresent()) {
			return new ResponseEntity<>(updatedDescription.get(), HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

}