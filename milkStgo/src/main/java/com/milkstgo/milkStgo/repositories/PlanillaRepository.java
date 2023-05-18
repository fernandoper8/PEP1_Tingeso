package com.milkstgo.milkStgo.repositories;

import com.milkstgo.milkStgo.entities.PlanillaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PlanillaRepository extends JpaRepository<PlanillaEntity, String> {

    @Query("select p from PlanillaEntity p where p.codigo = :codigo")
    ArrayList<PlanillaEntity> obtenerPlanillasPorProveedor(@Param("codigo") String codigo);

    @Query("select p from PlanillaEntity p where p.codigo = :codigo and p.comparado = 0")
    ArrayList<PlanillaEntity> obtenerPlanillaAnteriorProveedor(@Param("codigo") String codigo);
}
