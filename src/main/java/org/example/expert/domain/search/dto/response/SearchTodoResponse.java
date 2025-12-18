package org.example.expert.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchTodoResponse {

    private String title;
    private Long managerCount;
    private Long commentCount;
}