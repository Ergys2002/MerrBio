package com.app.merrbioapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@DynamicUpdate
@SQLDelete(sql = "UPDATE farmer SET deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "farmer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Farmer extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "farm_name")
    private String farmName;

    @Column(name = "farm_location")
    private String farmLocation;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "is_verified")
    private Boolean isVerified = false;
}