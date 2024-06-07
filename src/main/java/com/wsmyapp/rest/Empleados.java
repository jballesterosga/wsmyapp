package com.wsmyapp.rest;
import com.wsmyapp.repos.empleados.Empleado;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wsmyapp.repos.empleados.EmpleadoRepo;
import com.wsmyapp.repos.empleados.Familiar;
import com.wsmyapp.repos.empleados.FamiliarRepo;
import java.io.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
@RestController
public class Empleados
{
    private ResponseEntity<Map<String,String>> re;
    private HttpStatus http;
    private final EmpleadoRepo eRepo;
    private final FamiliarRepo fRepo;
    private final TransactionTemplate tt;
    @Autowired
    public Empleados(EmpleadoRepo repo,
                     FamiliarRepo fRepo,
                     PlatformTransactionManager tm)
    {
        this.eRepo = repo;
        this.fRepo = fRepo;
        tt = new TransactionTemplate(tm);
        tt.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }
    //EMPLEADOS
    @GetMapping(path = "/empleados",produces = "application/json")
    public ResponseEntity<List<Empleado>> todosEmpleados()
    {
        ResponseEntity<List<Empleado>> empleados;
        if (eRepo != null)
        {
            List<Empleado> l = eRepo.findAll(Sort.by(Direction.ASC,"nombre"));
            if (!l.isEmpty())
            {
                empleados = new ResponseEntity<>(l,
                                                 HttpStatus.OK);
            }
            else
            {
                empleados = new ResponseEntity<>(null,
                                                 HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            empleados = new ResponseEntity<>(null,
                                             HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return empleados;
    }
    @GetMapping(path = "/empleados-reporte",produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> reporteEmpleados()
    {
        ResponseEntity<byte[]> reporte;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (eRepo != null)
        {
            List<Empleado> l = eRepo.findAll(Sort.by(Direction.ASC,"nombre"));
            if (!l.isEmpty())
            {
                try
                {
                    SXSSFWorkbook libro = new SXSSFWorkbook();
                    SXSSFSheet hoja = libro.createSheet("Empleados");
                    SXSSFRow fila;
                    SXSSFCell celda;
                    //encabezado
                    fila = hoja.createRow(0);
                    celda = fila.createCell(0);
                    celda.setCellValue("IDENTIFICACION");
                    celda = fila.createCell(1);
                    celda.setCellValue("NOMBRE");
                    celda = fila.createCell(2);
                    celda.setCellValue("CORREO");
                    int f = 1;
                    for (Empleado e : l)
                    {
                        fila = hoja.createRow(f);
                        celda = fila.createCell(0);
                        celda.setCellValue(e.getIdentificacion());
                        celda = fila.createCell(1);
                        celda.setCellValue(e.getNombre());
                        celda = fila.createCell(2);
                        celda.setCellValue(e.getCorreo());
                        f++;
                    }
                    libro.write(baos);
                    reporte = new ResponseEntity<>(baos.toByteArray(),
                                                   HttpStatus.OK);
                }
                catch (Exception x)
                {
                    reporte = new ResponseEntity<>(null,
                                                   HttpStatus.INTERNAL_SERVER_ERROR);
                    LogManager.getLogger("wsmyapp").error("Error al generar reporte de empleados",x);
                }
            }
            else
            {
                reporte = new ResponseEntity<>(null,
                                               HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            reporte = new ResponseEntity<>(null,
                                           HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return reporte;
    }
    @GetMapping(path = "/empleados/identificacion/{identificacion}",produces = "application/json")
    public ResponseEntity<Empleado> empleadoPorIdentificacion(@PathVariable String identificacion)
    {
        ResponseEntity<Empleado> empleado;
        if (eRepo != null)
        {
            Empleado e = eRepo.findByIdentificacion(identificacion);
            if (e != null)
            {
                empleado = new ResponseEntity<>(e,
                                                HttpStatus.OK);
            }
            else
            {
                empleado = new ResponseEntity<>(null,
                                                HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            empleado = new ResponseEntity<>(null,
                                            HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return empleado;
    }
    @GetMapping(path = "/empleados/nombre/{nombre}",produces = "application/json")
    public ResponseEntity<List<Empleado>> empleadosPorNombre(@PathVariable String nombre)
    {
        http = HttpStatus.OK;
        ResponseEntity<List<Empleado>> empleados;
        if (eRepo != null)
        {
            List<Empleado> l = eRepo.findByNombreLike("%"+nombre+"%",
                                                      Sort.by(Direction.ASC,"nombre"));
            empleados = new ResponseEntity<>(l,
                                             http);
                
        }
        else
        {
            empleados = new ResponseEntity<>(null,
                                             HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return empleados;
    }
    @PostMapping(path = "/empleados",consumes = "application/x-www-form-urlencoded",produces = "application/json")
    public ResponseEntity<Map<String,String>> nuevoEmpleado(Empleado e)
    {
        return guardar("empleado",
                       e,
                       null);
    }
    @PutMapping(path = "/empleados/{id}",consumes = "application/x-www-form-urlencoded",produces = "application/json")
    public ResponseEntity<Map<String,String>> actualizarEmpleado(Empleado e,
                                                                 @PathVariable Integer id)
    {
        eRepo.findById(id).map(e1 ->
        {
            e1.setNombre(e.getNombre());
            e1.setCorreo(e.getCorreo());
            re = guardar("empleado",
                         e,
                         null);
            eRepo.save(e1);
            return null;
        }).orElseGet(() ->
        {
            e.setId(id);
            re = guardar("empleado",
                         e,
                         null);
            return null;
        });
        return re;
    }
    @DeleteMapping(path = "/empleados/{id}",produces = "application/json")
    public ResponseEntity<Map<String,String>> borrarEmpleado(@PathVariable Integer id)
    {
        return borrar("empleado",
                      id);
    }
    //FAMILIARES
    @GetMapping(path = "/familia/{identificacion}",produces = "application/json")
    public ResponseEntity<List<Familiar>> todosFamiliares(@PathVariable String identificacion)
    {
        ResponseEntity<List<Familiar>> familiares;
        if (eRepo != null)
        {
            List<Familiar> l = fRepo.findByIdentificacion(identificacion,
                                                           Sort.by(Direction.ASC,"detalle"));
            if (!l.isEmpty())
            {
                familiares = new ResponseEntity<>(l,
                                                  HttpStatus.OK);
            }
            else
            {
                familiares = new ResponseEntity<>(null,
                                                  HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            familiares = new ResponseEntity<>(null,
                                              HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return familiares;
    }
    @PostMapping(path = "/familia",consumes = "application/x-www-form-urlencoded",produces = "application/json")
    public ResponseEntity<Map<String,String>> nuevoFamiliar(Familiar f)
    {
        return guardar("familiar",
                       null,
                       f);
    }
    @PutMapping(path = "/familia/{id}",consumes = "application/x-www-form-urlencoded",produces = "application/json")
    public ResponseEntity<Map<String,String>> actualizarFamiliar(Familiar f,
                                                                 @PathVariable Integer id)
    {
        fRepo.findById(id).map(e1 ->
        {
            e1.setIdentificacion(f.getIdentificacion());
            e1.setDetalle(f.getDetalle());
            re = guardar("familiar",
                         null,
                         f);
            return null;
        }).orElseGet(() ->
        {
            f.setId(id);
            re = guardar("familiar",
                         null,
                         f);
            return null;
        });
        return re;
    }
    @DeleteMapping(path = "/familia/{id}",produces = "application/json")
    public ResponseEntity<Map<String,String>> borrarFamiliar(@PathVariable Integer id)
    {
        return borrar("familiar",
                      id);
    }
    //transacciones
    private ResponseEntity<Map<String,String>> guardar(String repo,
                                                       Empleado e,
                                                       Familiar f)
    {
        http = HttpStatus.OK;
        Map<String,String> body = new HashMap<>();
        boolean c = switch(repo)
        {
            case "empleado" -> (e != null);
            case "familiar" -> (f != null);
            default -> false;
        };
        if (c)
        {
            tt.execute(new TransactionCallbackWithoutResult()
            {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status)
                {
                    String msj = "";
                    try
                    {
                        switch (repo)
                        {
                            case "empleado" ->
                            {
                                eRepo.save(e);
                                msj = "Empleado "+e.getNombre()+" creado/actualizado correctamente";
                            }
                            case "familiar" ->
                            {
                                fRepo.save(f);
                                msj = "Familiar "+f.getDetalle()+" creado/actualizado correctamente";
                            }
                        }
                        body.put("mensaje",msj);
                        LogManager.getRootLogger().info(msj);
                    }
                    catch (Exception x)
                    {
                        msj = "Datos no pudieron ser guardados/actualizados -> "+repo;
                        status.setRollbackOnly();
                        http = HttpStatus.INTERNAL_SERVER_ERROR;
                        body.put("mensaje",msj);
                        LogManager.getLogger("wsmyapp").error(msj,x);
                    }
                }
            });
        }
        else
        {
            body.put("mensaje","Objetos de datos ("+repo+") no es correcto");
            http = HttpStatus.BAD_REQUEST;
        }
        re = new ResponseEntity<>(body,http);
        return re;
    }
    private ResponseEntity<Map<String,String>> borrar(String repo,
                                                      Integer id)
    {
        http = HttpStatus.OK;
        Map<String,String> body = new HashMap<>();
        boolean c = switch(repo)
        {
            case "empleado" -> (eRepo.findById(id) != null);
            case "familiar" -> (fRepo.findById(id) != null);
            default -> false;
        };
        if (c)
        {
            tt.execute(new TransactionCallbackWithoutResult()
            {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status)
                {
                    String msj = "";
                    try
                    {
                        switch (repo)
                        {
                            case "empleado" ->
                            {
                                eRepo.deleteById(id);
                                msj = "Empleado "+id+" borrado correctamente";
                            }
                            case "familiar" ->
                            {
                                fRepo.deleteById(id);
                                msj = "Familiar "+id+" borrado correctamente";
                            }
                        }
                        body.put("mensaje",msj);
                        LogManager.getRootLogger().info(msj);
                    }
                    catch (Exception x)
                    {
                        msj = "Datos no pudieron ser borrados -> "+repo;
                        status.setRollbackOnly();
                        http = HttpStatus.INTERNAL_SERVER_ERROR;
                        body.put("mensaje",msj);
                        LogManager.getLogger("wsmyapp").error(msj,x);
                    }
                }
            });
        }
        else
        {
            body.put("mensaje","Objetos de datos ("+repo+") no existe");
            http = HttpStatus.BAD_REQUEST;
        }
        re = new ResponseEntity<>(body,http);
        return re;
    }
}