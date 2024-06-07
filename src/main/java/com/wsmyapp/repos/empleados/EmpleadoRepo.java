package com.wsmyapp.repos.empleados;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EmpleadoRepo extends JpaRepository<Empleado,Integer>
{
    Empleado findByIdentificacion(String identificacion);
    List<Empleado> findByNombreLike(String nombre,
                                    Sort sort);
}
