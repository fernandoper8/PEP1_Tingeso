package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcopioRepository extends JpaRepository<AcopioEntity, String> {
}