package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);
}
