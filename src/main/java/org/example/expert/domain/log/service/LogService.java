package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final ManagerRepository managerRepository;
    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long loggingStartSaveManager(Long todoId, Long managerId) {

        Log log = new Log(todoId, managerId, false);

        logRepository.save(log);

        return log.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loggingEndSaveManager(Long id, Long managerId) {

        Log log = logRepository.findById(id).orElseThrow(() -> new IllegalStateException("로그가 존재하지 않습니다."));
        boolean isSuccess = managerRepository.existsByTodo_Id(log.getTodoId());

        log.updateLog(managerId, isSuccess);

        logRepository.save(log);
    }
}