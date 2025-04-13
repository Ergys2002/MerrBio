package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, UUID> {

    Optional<Farmer> findByUserId(UUID userId);

    List<Farmer> findByIsVerified(Boolean isVerified);

    boolean existsByFarmName(String farmName);

    @Query("SELECT f FROM Farmer f JOIN f.user u JOIN u.userInfo ui WHERE " +
            "LOWER(f.farmName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(f.farmLocation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(ui.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(ui.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Farmer> searchFarmers(@Param("keyword") String keyword);


    boolean existsByUserId(UUID userId);

    long countByIsVerified(Boolean isVerified);

    List<Farmer> findByFarmNameContainingIgnoreCase(String farmName);

    Farmer findByUserEmail(String currentUserEmail);
}