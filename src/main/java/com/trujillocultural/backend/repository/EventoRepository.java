package com.trujillocultural.backend.repository;

import com.trujillocultural.backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByCategoria(String categoria);

    List<Evento> findByEsGratuito(boolean esGratuito);

    List<Evento> findByTipo(String tipo);
}