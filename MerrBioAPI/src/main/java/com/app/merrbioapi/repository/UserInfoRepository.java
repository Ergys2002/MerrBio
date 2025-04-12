package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);

}
