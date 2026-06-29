package com.trujillocultural.backend;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trujillocultural.backend.model.Evento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class EventoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private ObjectMapper objectMapper;

    @BeforeEach
    void crearEventoDePrueba() throws Exception {
        if (this.mockMvc == null) {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        }
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        Evento evento = new Evento();
        evento.setTitulo("Evento de prueba Marinera");
        evento.setDescripcion("Evento creado para testing automatizado");
        evento.setLugar("Coliseo de prueba");
        evento.setCategoria("danza");
        evento.setTipo("tradicional");
        evento.setEsGratuito(false);
        evento.setPrecio(20.0);

        mockMvc.perform(post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evento)));
    }

    @Test
    void listarTodosLosEventos_debeResponder200() throws Exception {
        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk());
    }

    @Test
    void filtrarPorCategoriaDanza_debeRetornarSoloEventosDeDanza() throws Exception {
        mockMvc.perform(get("/api/eventos").param("categoria", "danza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("danza"));
    }

    @Test
    void filtrarPorGratuito_debeRetornarSoloEventosGratuitos() throws Exception {
        mockMvc.perform(get("/api/eventos").param("gratuito", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].esGratuito").value(false));
    }

    @Test
    void crearEvento_conDatosValidos_debeRetornar200() throws Exception {
        Evento nuevo = new Evento();
        nuevo.setTitulo("Festival de prueba JUnit");
        nuevo.setLugar("Plaza de prueba");
        nuevo.setCategoria("cultural");
        nuevo.setTipo("municipal");
        nuevo.setEsGratuito(true);
        nuevo.setPrecio(0.0);

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Festival de prueba JUnit"));
    }
}