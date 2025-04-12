package com.app.merrbioapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicUpdate
@SQLDelete(sql = "UPDATE conversation SET deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "conversation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conversation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product relatedProduct;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}