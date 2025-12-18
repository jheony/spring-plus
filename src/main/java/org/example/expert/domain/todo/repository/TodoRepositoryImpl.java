package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.search.dto.response.SearchTodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> getTodo(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo t = queryFactory.selectFrom(todo)
                .join(todo.user, user)
                .fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(t);
    }


    @Override
    public Page<SearchTodoResponse> getTodoWithCondition(
            String keyword,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String nickname,
            Pageable pageable
    ) {

        List<SearchTodoResponse> todos = queryFactory
                .select(Projections.constructor(SearchTodoResponse.class,
                        todo.title,
                        manager.countDistinct(),
                        comment.countDistinct()
                ))
                .from(todo)
                .leftJoin(manager).on(manager.todo.id.eq(todo.id))
                .leftJoin(comment).on(comment.todo.id.eq(todo.id))
                .leftJoin(user).on(user.id.eq(manager.user.id))
                .where(
                        keywordEq(keyword),
                        startDateEq(startDate),
                        endDateEq(endDate),
                        nicknameEq(nickname)
                )
                .groupBy(todo.id)
                .offset(pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        Long todoCnt = queryFactory
                .select(todo.count())
                .from(todo)
                .leftJoin(manager).on(manager.todo.id.eq(todo.id))
                .leftJoin(comment).on(comment.todo.id.eq(todo.id))
                .leftJoin(user).on(user.id.eq(manager.user.id))
                .where(
                        keywordEq(keyword),
                        startDateEq(startDate),
                        endDateEq(endDate),
                        nicknameEq(nickname)
                )
                .fetchOne();

        if (todoCnt == null) {
            todoCnt = 0L;
        }

        return new PageImpl<>(todos, pageable, todoCnt);
    }

    private BooleanExpression keywordEq(String keyword) {
        return (keyword != null) ? todo.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression startDateEq(LocalDateTime startDate) {
        return (startDate != null) ? todo.createdAt.goe(startDate) : null;
    }

    private BooleanExpression endDateEq(LocalDateTime endDate) {
        return (endDate != null) ? todo.createdAt.loe(endDate) : null;
    }

    private BooleanExpression nicknameEq(String nickname) {
        return (nickname != null) ? user.nickname.containsIgnoreCase(nickname) : null;
    }
}