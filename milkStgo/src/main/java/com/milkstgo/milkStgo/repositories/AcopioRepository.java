package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.security.PublicKey;
import java.util.ArrayList;

@Repository
public interface AcopioRepository extends JpaRepository<AcopioEntity, String> {

    @Query("select a from AcopioEntity a where a.id_proveedor = :proveedor")
    ArrayList<AcopioEntity> obtenerAcopiosPorProveedor(@Param("proveedor") String proveedor);

}