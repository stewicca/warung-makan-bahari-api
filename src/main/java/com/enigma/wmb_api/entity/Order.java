package com.enigma.wmb_api.entity;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.OrderStatus;
import com.enigma.wmb_api.constant.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name= Constant.ORDER_TABLE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @PrePersist()
    public void prePersist() {
        this.transactionDate = LocalDateTime.now();
    }

}
