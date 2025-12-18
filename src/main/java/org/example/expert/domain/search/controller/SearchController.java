package org.example.expert.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.search.dto.response.SearchTodoResponse;
import org.example.expert.domain.search.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public Page<SearchTodoResponse> getTodos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String nickname,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        return searchService.getTodos(keyword, startDate, endDate, nickname, pageable);
    }
}