package com.canoo.dolphin.todo.server;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoItemRepository extends JpaRepository<ToDoItemDTO, Long> {
}
