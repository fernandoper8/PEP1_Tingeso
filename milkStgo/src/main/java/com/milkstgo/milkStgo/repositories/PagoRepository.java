package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<PagoEntity, String> {
}
