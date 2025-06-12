package com.example.signmanager.repository;

import com.example.signmanager.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리 인터페이스
 * - Spring Data JPA가 자동으로 구현체를 생성해줍니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository를 상속받으면 User 엔티티와 Long 타입의 ID를 사용하여 기본적인 CRUD(Create, Read, Update, Delete)
    // 메서드(save, findById, findAll, delete 등)를 자동으로 제공받습니다.

    /**
     * 이메일(email)으로 User 엔티티를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 조회된 User 엔티티를 Optional로 감싸 반환
     */
    Optional<User> findByEmail(String email); // ⭐ 새로 추가된 메서드 ⭐

    /**
     * 특정 사용자 이름이 데이터베이스에 이미 존재하는지 확인합니다.
     * @param username 확인할 사용자 이름
     * @return 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByUsername(String username);

    /**
     * 특정 이메일 주소가 데이터베이스에 이미 존재하는지 확인합니다.
     * @param email 확인할 이메일 주소
     * @return 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByEmail(String email);
}
