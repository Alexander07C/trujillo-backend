package com.trujillocultural.backend.service;

import com.trujillocultural.backend.model.Evento;
import com.trujillocultural.backend.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {
    @Autowired
    private EventoRepository eventoRepository;
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }
    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }
    public List<Evento> filtrarPorCategoria(String categoria) {
        return eventoRepository.findByCategoria(categoria);
    }
    public List<Evento> filtrarPorGratuito(boolean esGratuito) {
        return eventoRepository.findByEsGratuito(esGratuito);
    }
    public Evento guardar(Evento evento) {
        return eventoRepository.save(evento);
    }
    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
    public void sincronizarDesdeJson(List<Evento> eventosDesdeJson) {
        for (Evento nuevo : eventosDesdeJson) {
            boolean existe = eventoRepository.findAll().stream()
                    .anyMatch(e -> e.getTitulo().equals(nuevo.getTitulo()));
            if (!existe) {
                eventoRepository.save(nuevo);
            }
        }
    }
}