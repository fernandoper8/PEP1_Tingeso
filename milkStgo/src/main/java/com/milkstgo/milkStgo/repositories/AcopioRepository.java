package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AcopioRepository extends JpaRepository<AcopioEntity, String> {
    //public ArrayList<AcopioEntity> findById_proveedor(String codigo);
}
