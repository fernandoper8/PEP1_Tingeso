package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.DatosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatosRepository extends JpaRepository<DatosEntity, String> {

}
