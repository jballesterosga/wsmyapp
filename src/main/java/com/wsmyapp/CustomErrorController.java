package com.wsmyapp;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class CustomErrorController implements ErrorController
{
    @RequestMapping(path="/error",produces = "application/json")
    public ResponseEntity<Map<String,String>> error(HttpServletRequest request)
    {
        HttpStatus http = HttpStatus.NOT_FOUND;
        Map<String,String> body = new HashMap<>();
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null)
        {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value())
            {
                body.put("mensaje","Recurso solicitado no encontrado");
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
            {
                http = HttpStatus.INTERNAL_SERVER_ERROR;
                body.put("mensaje","Error en servidor de aplicaciones");
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value())
            {
                http = HttpStatus.FORBIDDEN;
                body.put("mensaje","Petici\u00f3n incorrecta");
            }
            else if (statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            {
                http = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                body.put("mensaje","Content-type incorrecto");
            }
            else
            {
                body.put("mensaje","Error: "+statusCode);
            }
            LogManager.getLogger("wsmyapp").error("Error en petici\u00f3n "+Integer.valueOf(status.toString()),new Exception());
        }
        return new ResponseEntity<>(body,http);
    }
}