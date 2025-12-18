package org.example.expert.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.search.dto.response.SearchTodoResponse;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public Page<SearchTodoResponse> getTodos(String keyword,
                                             LocalDateTime startDate,
                                             LocalDateTime endDate,
                                             String nickname,
                                             Pageable pageable
    ) {
        return todoRepository.getTodoWithCondition(keyword, startDate, endDate, nickname, pageable);
    }
}