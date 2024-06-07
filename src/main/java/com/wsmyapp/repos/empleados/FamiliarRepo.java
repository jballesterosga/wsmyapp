package com.wsmyapp.repos.empleados;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FamiliarRepo extends JpaRepository<Familiar,Integer>
{
    List<Familiar> findByIdentificacion(String identificacion,
                                        Sort sort);
}