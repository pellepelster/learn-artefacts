package io.pelle.todo;

import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.UUID;

public class Todo implements Serializable {

	private UUID uuid = UUID.randomUUID();

	private String description;

	private boolean complete;

	public Todo() {
	}

	public Todo(UUID uuid, String description, boolean complete) {
		this.uuid = uuid;
		this.description = description;
		this.complete = complete;
	}

	public Todo(String description)
	{
		this(UUID.randomUUID(), description, false);
	}

	public UUID getUuid() {
		return uuid;
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
		return "[ id=" + this.uuid + ", description=" + this.description + ", complete=" + this.complete + " ]";
	}

}