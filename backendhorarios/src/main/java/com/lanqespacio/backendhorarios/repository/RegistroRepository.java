package com.lanqespacio.backendhorarios.repository;

import com.lanqespacio.backendhorarios.model.Registro;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RegistroRepository extends CrudRepository<Registro, Long> {

    @Query(value =
    """
        SELECT
            ROW_NUMBER() OVER (ORDER BY calificacion DESC) AS top,
            apodo,
            fecha,
            exigencias,
            SUBSTR(CAST(calificacion AS TEXT), 1, 14) AS calificacion
        FROM
            Registro
        WHERE
            apodo != 'OCULTAR';
    """, nativeQuery = true)
    List<Object[]> topHorarios();

}
