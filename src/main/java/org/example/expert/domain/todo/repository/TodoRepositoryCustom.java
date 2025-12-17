package org.example.expert.domain.todo.repository;

import org.example.expert.domain.search.dto.response.SearchTodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepositoryCustom {

    Optional<Todo> getTodo(Long todoId);

    Page<SearchTodoResponse> getTodoWithCondition(String keyword, LocalDateTime startDae, LocalDateTime endDate, String nickname, Pageable pageable);
}