package com.niqdev.authserver.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.niqdev.authserver.dto.user.UserSearchCriteria;
import com.niqdev.authserver.entity.User;
import com.niqdev.authserver.repository.admin.UserRepository;
import com.niqdev.authserver.support.factory.UserTestFactory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 若使用真實 DB
public class UserSpecificationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // 清空資料庫
        userRepository.deleteAll();

        // 使用 UserTestFactory 來建立測試用資料
        User user1 = UserTestFactory.createUser("alice", "alice@example.com");
        user1.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        user1.setCreatedBy("admin");

        User user2 = UserTestFactory.createUser("bob", "bob@example.com");
        user2.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        user2.setCreatedBy("system");
        user2.setEnabled(false);

        // 儲存資料
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    @Test
    void testSearchByCreatedAtRange() {
        // 使用 UserSearchCriteria 並設定時間範圍
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setCreatedAtFrom(LocalDateTime.of(2023, 12, 31, 0, 0));
        criteria.setCreatedAtTo(LocalDateTime.of(2024, 12, 31, 0, 0));

        // 使用 Specification 搜尋
        Page<User> result = userRepository.findAll(
            UserSpecification.withCriteria(criteria), PageRequest.of(0, 10));

        // 驗證結果
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("bob");
    }

    @Test
    void testSearchByUsername() {
        // 使用 UserSearchCriteria 並設定篩選條件
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsername("alice");

        // 使用 Specification 搜尋
        Page<User> result = userRepository.findAll(
            UserSpecification.withCriteria(criteria), PageRequest.of(0, 10));

        // 驗證結果
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("alice");
    }

    @Test
    void testSearchByEmail() {
        // 使用 UserSearchCriteria 並設定篩選條件
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setEmail("bob@example.com");

        // 使用 Specification 搜尋
        Page<User> result = userRepository.findAll(
            UserSpecification.withCriteria(criteria), PageRequest.of(0, 10));

        // 驗證結果
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("bob");
    }

    @Test
    void testSearchByStatus() {
        // 使用 UserSearchCriteria 並設定篩選條件
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setEnabled(true);
        criteria.setAccountNonLocked(true);

        // 使用 Specification 搜尋
        Page<User> result = userRepository.findAll(
            UserSpecification.withCriteria(criteria), PageRequest.of(0, 10));

        // 驗證結果
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("alice");
    }

    // 你可以持續加上各種條件組合的測試
}
