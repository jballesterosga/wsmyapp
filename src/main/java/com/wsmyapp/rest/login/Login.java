package com.wsmyapp.rest.login;
import com.wsmyapp.repos.usuarios.Usuario;
import com.wsmyapp.repos.usuarios.UsuarioRepo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class Login
{
    private final UsuarioRepo uRepo;
    @Autowired
    public Login(UsuarioRepo uRepo)
    {
        this.uRepo = uRepo;
    }
    @PostMapping(path = "/login",consumes = "application/x-www-form-urlencoded",produces = "application/json")
    public ResponseEntity<Map<String,String>> login(@RequestParam(name="usuario",defaultValue = "")String usuario,
                                                    @RequestParam(name="contrasena",defaultValue = "")String contrasena)
    {
        Map<String,String> body = new HashMap<>();
        HttpStatus http = HttpStatus.OK;
        if (!usuario.isEmpty() &&
            !contrasena.isEmpty())
        {
            Usuario usr = uRepo.findByUsuarioAndContrasena(usuario,
                                                           contrasena);
            if (usr != null)
            {
                body.put("usuario",usuario);
                body.put("token",getJWTToken(usuario));
            }
            else
            {
                body.put("usuario",usuario);
                body.put("token","?");
                http = HttpStatus.UNAUTHORIZED;
            }
        }
        else
        {
            body.put("error","usuario");
            body.put("mensaje","Parametros incorrectos");
            http = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(body,http);
    }
    @GetMapping(path = "/validar-token",produces = "application/json")
    public ResponseEntity<Map<String,String>> validarToken()
    {
        Map<String,String> body = new HashMap<>();
        HttpStatus http = HttpStatus.OK;
        body.put("sesion","OK");
        return new ResponseEntity<>(body,http);
    }
    private String getJWTToken(String usuario)
    {
        String secretKey = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTcxNjU3OTg0NywiaWF0IjoxNzE2NTc5ODQ3fQ.XqMLajhY60wKq28DjzrgZSHeOs6FudsVGiq35Fyx78Q";
        List<GrantedAuthority> ga = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        DateTime issued = new DateTime(new Date(System.currentTimeMillis()));
        DateTime expiration = issued.plusMinutes(30);
        String token = Jwts.builder()
                           .id(UUID.randomUUID().toString())
                           .subject(usuario)
                           .claim("authorities",
                                  ga.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList()))
                           .issuedAt(issued.toDate())
                           .expiration(expiration.toDate())
                           .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                           .compact();
        return "Bearer "+token;
    }
}