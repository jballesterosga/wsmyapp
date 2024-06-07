package com.wsmyapp.repos.empleados;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name="Empleados")
public class Empleado
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter @Getter
    private int id;
    @Setter @Getter
    private String identificacion;
    @Setter @Getter
    private String nombre;
    @Setter @Getter
    private String correo;
    protected Empleado(){}
    public Empleado(String identificacion,
                    String nombre,
                    String correo)
    {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
    }
    @Override
    public boolean equals(Object o)
    {

      if (this == o)
      {
          return true;
      }
      if (!(o instanceof Empleado))
      {
          return false;
      }
      Empleado e = (Empleado)o;
      return Objects.equals(this.id, e.id) &&
             Objects.equals(this.identificacion, e.identificacion) &&
             Objects.equals(this.nombre, e.nombre) &&
             Objects.equals(this.correo, e.correo);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(this.id,
                            this.identificacion,
                            this.nombre,
                            this.correo);
    }
}