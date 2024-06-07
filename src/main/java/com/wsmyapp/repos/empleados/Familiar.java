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
@Table(name="Familiares")
public class Familiar
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter @Getter
    private int id;
    @Setter @Getter
    private String identificacion;
    @Setter @Getter
    private String detalle;
    protected Familiar(){}
    public Familiar(String identificacion,
                    String detalle)
    {
        this.identificacion = identificacion;
        this.detalle = detalle;
    }
    @Override
    public boolean equals(Object o)
    {

      if (this == o)
      {
          return true;
      }
      if (!(o instanceof Familiar))
      {
          return false;
      }
      Familiar e = (Familiar)o;
      return Objects.equals(this.id, e.id) &&
             Objects.equals(this.identificacion,e.identificacion) &&
             Objects.equals(this.detalle,e.detalle);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id,
                            this.identificacion,
                            this.detalle);
    }
}