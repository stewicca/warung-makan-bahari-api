package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.MenuImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, String> {
}
