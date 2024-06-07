package com.wsmyapp.repos.usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioRepo extends JpaRepository<Usuario,String>
{
    Usuario findByUsuarioAndContrasena(String usuario,
                                       String contrasena);
}