package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.AcopioEntity;
import com.milkstgo.milkStgo.entities.DatosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DatosRepository extends JpaRepository<DatosEntity, String> {

    @Query("select d from DatosEntity d where d.id_proveedor = :codigo")
    ArrayList<DatosEntity> obtenerDatosPorProveedor(@Param("codigo") String codigo);
}
