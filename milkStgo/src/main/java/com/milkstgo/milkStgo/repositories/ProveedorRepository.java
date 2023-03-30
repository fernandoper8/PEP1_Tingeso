package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<ProveedorEntity, String> {

}
