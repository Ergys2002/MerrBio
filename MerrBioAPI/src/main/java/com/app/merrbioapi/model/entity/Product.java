package com.app.merrbioapi.model.entity;

import com.app.merrbioapi.model.enums.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicUpdate
@SQLDelete(sql = "UPDATE product SET deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private Unit unit;

    @Column(name = "min_available_quantity", nullable = false)
    private Double minAvailableQuantity;

    @Column(name = "max_available_quantity", nullable = false)
    private Double maxAvailableQuantity;

    @Column(name = "minimum_order_quantity")
    private Double minimumOrderQuantity;

    @OneToMany(mappedBy = "product")
    private List<ProductCategory> category;

    @Column(name = "is_organic")
    private Boolean isOrganic = false;

    @Column(name = "is_in_stock")
    private Boolean isInStock = true;

    @OneToMany(mappedBy = "product")
    private List<Image> imageUrls = new ArrayList<>();

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
}