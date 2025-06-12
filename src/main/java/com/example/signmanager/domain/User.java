package com.example.signmanager.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User 엔티티 (데이터베이스 테이블과 매핑되는 객체)
 * - 회원 정보를 나타냅니다.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용을 위한 protected 기본 생성자
@AllArgsConstructor // ⭐ 이 어노테이션을 추가하세요. 모든 필드를 매개변수로 받는 생성자를 자동으로 만들어줍니다. ⭐
@Builder // 이 클래스에 대한 빌더 패턴 코드를 자동으로 생성해 줍니다.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String username;

    // ⭐ 중요: @AllArgsConstructor를 추가했으므로,
    // 이 수동으로 만든 생성자는 이제 필요하지 않습니다. 제거하는 것이 좋습니다.
    // 만약 이 생성자를 다른 용도로 남기고 싶다면, private 또는 protected로 변경하여
    // 외부에서 직접 호출하는 것을 제한하는 것이 일반적입니다.
    // @Builder를 클래스에 붙이면, Lombok이 모든 필드를 매개변수로 받는 생성자를 자동으로 만들어주기 때문에
    // 수동으로 똑같은 생성자를 만들 필요가 없습니다.
    // public User(String email, String password, String name) {
    //     this.email = email;
    //     this.password = password;
    //     this.name = name;
    // }
}
