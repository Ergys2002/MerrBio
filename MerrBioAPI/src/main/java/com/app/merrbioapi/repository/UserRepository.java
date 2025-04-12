package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) AND (:email IS NULL OR u.email LIKE %:email%) AND u.role != 'ADMIN'")
    Page<User> findUsersByFilters(@Param("role") Role role, @Param("email") String email, Pageable pageable);
}
