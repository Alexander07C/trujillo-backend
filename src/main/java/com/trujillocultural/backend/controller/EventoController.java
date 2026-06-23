package com.trujillocultural.backend.controller;

import com.trujillocultural.backend.model.Evento;
import com.trujillocultural.backend.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // GET /api/eventos
    @GetMapping
    public List<Evento> listarEventos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean gratuito) {

        if (categoria != null) {
            return eventoService.filtrarPorCategoria(categoria);
        }
        if (gratuito != null) {
            return eventoService.filtrarPorGratuito(gratuito);
        }
        return eventoService.listarTodos();
    }

    // GET /api/eventos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEvento(@PathVariable Long id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/eventos
    @PostMapping
    public Evento crearEvento(@RequestBody Evento evento) {
        return eventoService.guardar(evento);
    }

    // DELETE /api/eventos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<String> sincronizarEventos() {
        try {
            String rutaArchivo = "data/eventos.json";
            File archivo = new File(rutaArchivo);

            if (!archivo.exists()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error: El archivo " + rutaArchivo + " no existe. Verifica la ruta del archivo de datos.");
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            List<Evento> eventosDesdeJson = mapper.readValue(
                    archivo,
                    mapper.getTypeFactory().constructCollectionType(List.class, Evento.class)
            );

            if (eventosDesdeJson == null || eventosDesdeJson.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("El archivo JSON no contiene eventos o está vacío.");
            }

            eventoService.sincronizarDesdeJson(eventosDesdeJson);

            return ResponseEntity.ok("Sincronización completada. " + eventosDesdeJson.size() + " eventos procesados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error durante la sincronización: " + e.getMessage());
        }
    }
}