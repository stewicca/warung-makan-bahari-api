package com.enigma.wmb_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_menu_image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MenuImage extends File {
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
}