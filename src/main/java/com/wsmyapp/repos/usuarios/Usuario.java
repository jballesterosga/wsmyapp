package com.wsmyapp.repos.usuarios;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name="Usuarios")
public class Usuario
{
    @Id @Setter @Getter
    private String usuario;
    @Setter @Getter
    private String contrasena;
    protected Usuario(){}
    public Usuario(String usuario,
                   String contrasena)
    {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }
    @Override
    public boolean equals(Object o)
    {

      if (this == o)
      {
          return true;
      }
      if (!(o instanceof Usuario))
      {
          return false;
      }
      Usuario e = (Usuario)o;
      return Objects.equals(this.usuario, e.usuario) &&
             Objects.equals(this.contrasena, e.contrasena);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(this.usuario,
                            this.contrasena);
    }
}