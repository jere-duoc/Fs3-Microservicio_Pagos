package DuocQuin.Pagos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import DuocQuin.Pagos.model.PagosModel;
import DuocQuin.Pagos.service.PagosService;

@RestController
@RequestMapping("/api/sueldos")
@CrossOrigin(origins = "*")
public class PagosController {

    @Autowired
    private PagosService pagosService;
    

    @PostMapping
    public PagosModel crear(@RequestBody PagosModel sueldo){
        return pagosService.save(sueldo);
    }

    @GetMapping
    public List<PagosModel> listar(){
        return pagosService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagosModel> obtenerPorId(@PathVariable Long id){
        Optional<PagosModel> sueldo = pagosService.findById(id);

        return sueldo
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public PagosModel actualizar(@PathVariable Long id, @RequestBody PagosModel sueldo){
        return pagosService.update(id, sueldo);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id){
        pagosService.deleteById(id);
        return "Sueldo eliminado correctamente";
    }
}