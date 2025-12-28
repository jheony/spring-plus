package org.example.expert.domain.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class AuthServiceTest {

    final int TOTAL_COUNT = 5000000;
    final int BATCH_SIZE = 10000;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("유저 데이터 500만건 생성")
    void createManyUser() throws InterruptedException {

        String sql = "INSERT INTO USERS(`email`, `password`, `nickname`, `user_role`) VALUES (?, ?, ?, ?)";

        for (int i = 0; i < TOTAL_COUNT / BATCH_SIZE; i++) {

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, "email" + Math.random() + "@test.com");
                    ps.setString(2, "password");
                    ps.setString(3, "nickname" + Math.random());
                    ps.setString(4, "USER");
                }

                @Override
                public int getBatchSize() {
                    return BATCH_SIZE;
                }
            });
        }
    }

    @Test
    @DisplayName("StringBuilder로 유저 데이터 500만건 생성")
    void createManyUserWithStringBuilder() {

        // Builder로 인서트문 쌓기
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("INSERT INTO USERS(`email`, `password`, `nickname`, `user_role`) VALUES ");

        for (int i = 0; i < TOTAL_COUNT; i++) {
            sqlSb.append("(");
            sqlSb.append("\"email").append(i).append("@test.com\", ");
            sqlSb.append("\"password\", ");
            sqlSb.append("\"nickname").append(i).append("\", ");
            sqlSb.append("\"USER\"").append(")");

            if (i % BATCH_SIZE == 0) {
                jdbcTemplate.execute(String.valueOf(sqlSb));

                sqlSb.setLength(0);
                sqlSb.append("INSERT INTO USERS(`email`, `password`, `nickname`, `user_role`) VALUES ");

            } else {
                sqlSb.append(", ");
            }
        }
    }

    @Test
    @DisplayName("Multi-Thread로 유저 데이터 500만건 생성")
    void createManyUserWithMultiThread() throws InterruptedException {

        String sql = "INSERT INTO USERS(`email`, `password`, `nickname`, `user_role`) VALUES (?, ?, ?, ?)";

        // 멀티쓰레드
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(TOTAL_COUNT / BATCH_SIZE);

        for (int i = 0; i < TOTAL_COUNT / BATCH_SIZE; i++) {
            executorService.execute(() -> {
                        try {
                            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                                @Override
                                public void setValues(PreparedStatement ps, int i) throws SQLException {
                                    ps.setString(1, "email" + Math.random() + "@test.com");
                                    ps.setString(2, "password");
                                    ps.setString(3, "nickname" + Math.random());
                                    ps.setString(4, "USER");
                                }

                                @Override
                                public int getBatchSize() {
                                    return BATCH_SIZE;
                                }
                            });
                        } finally {
                            latch.countDown();
                        }
                    }
            );
        }
        latch.await();
        executorService.shutdown();
    }

    @Test
    @DisplayName("PreparedStatement로 유저 데이터 500만건 생성")
    void createManyUserWithPreparedStatement() throws SQLException {

        String sql = "INSERT INTO USERS(`email`, `password`, `nickname`, `user_role`) VALUES (?, ?, ?, ?)";

        DataSource dataSource = jdbcTemplate.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql);

            for (int i = 1; i <= TOTAL_COUNT; i++) {
                String email = "email" + i + "@test.com";
                String password = "password";
                String nickname = "nickname" + i;
                String userRole = "USER";

                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, nickname);
                ps.setString(4, userRole);
                ps.addBatch();

                if (i % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    connection.commit();
                    ps.clearBatch();
                }
            }
        }
    }
}