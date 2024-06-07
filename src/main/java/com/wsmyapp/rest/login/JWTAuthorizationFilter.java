package com.wsmyapp.rest.login;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
public class JWTAuthorizationFilter extends OncePerRequestFilter
{
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    private final String SECRET = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTcxNjU3OTg0NywiaWF0IjoxNzE2NTc5ODQ3fQ.XqMLajhY60wKq28DjzrgZSHeOs6FudsVGiq35Fyx78Q";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
    {
        try
        {
            if (checkJWTToken(request))
            {
                Claims claims = validateToken(request);
                if (claims.get("authorities") != null)
                {
                    setUpSpringAuthentication(claims);
                }
                else
                {
                    SecurityContextHolder.clearContext();
                }
            }
            else
            {
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request,
                           response);
        }
        catch (Exception e)
        {
            LogManager.getLogger("wsmyapp").error("Error al validar token",e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try
            {
                response.getWriter().write("""
                                           {
                                                "error":"token",
                                                "mensaje":"Error de validaci\u00f3n de token"
                                           }
                                           """);
            }
            catch (IOException ex)
            {
                LogManager.getLogger("wsmyapp").error("Error al enviar respuesta de verificaci\u00f3n de token",ex);
            }
        }
    }
    private Claims validateToken(HttpServletRequest request)
    {
        String token = request.getHeader(HEADER).replace(PREFIX,"");
        SecretKey sk = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        JwtParser parser = Jwts.parser()
                               .verifyWith(sk)
                               .build();
        return parser.parseSignedClaims(token).getPayload();
    }
    private void setUpSpringAuthentication(Claims claims)
    {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>)claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(),
                                                                                           null,
                                                                                           authorities.stream()
                                                                                                      .map(SimpleGrantedAuthority::new)
                                                                                                      .collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    private boolean checkJWTToken(HttpServletRequest request)
    {
        String header = request.getHeader(HEADER);
        return !(header == null || !header.startsWith(PREFIX));
    }
}
