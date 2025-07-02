package com.puenteblanco.pb.repository;

import com.puenteblanco.pb.entity.Cita;
import com.puenteblanco.pb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByUsuario(User usuario);

    List<Cita> findByUsuarioCorreoAndEstadoIgnoreCase(String correo, String estado);

    List<Cita> findAllByUsuarioCorreoAndEstado(String correo, String estado);

    @Query("SELECT DISTINCT c.usuario FROM Cita c WHERE c.veterinario.usuario.id = :vetId AND c.estado = 'COMPLETADA'")
    List<User> findClientesUnicosPorVeterinario(@Param("vetId") Long vetId);

    List<Cita> findByVeterinarioIdAndFecha(Long veterinarioId, LocalDate fecha);

    List<Cita> findByVeterinarioIdAndFechaBetween(Long veterinarioId, LocalDate desde, LocalDate hasta);

    List<Cita> findByVeterinarioIdAndEstado(Long vetId, String estado);

    List<Cita> findByVeterinarioIdAndFechaBetweenAndEstado(Long vetId, LocalDate desde, LocalDate hasta, String estado);
    
    List<Cita> findByVeterinarioIdAndFechaAndEstado(Long veterinarioId, LocalDate fecha, String estado);

    List<Cita> findByFechaBetween(LocalDate startDate, LocalDate endDate); // REPORTES

    List<Cita> findByIntern_IdAndEstado(Long internId, String estado); // Para interno

    @Query("SELECT c FROM Cita c WHERE c.intern.id = :internId AND (c.estado = 'COMPLETADA' OR c.estado = 'PAGADA')")
    List<Cita> findCitasValidadasPorIntern(@Param("internId") Long internId);; // Para Interno

    List<Cita> findByEstado(String estado); // PARA EVALUADAS

    int countByVeterinarioIdAndFecha(Long vetId, LocalDate fecha);

    int countByVeterinarioIdAndFechaBetweenAndEstado(Long vetId, LocalDate desde, LocalDate hasta, String estado);

    int countByFecha(LocalDate fecha); // Admin 

    int countByEstadoAndFechaBetween(String estado, LocalDate inicio, LocalDate fin); // Admin

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.estado = 'COMPLETADA' AND c.id NOT IN (SELECT a.cita.id FROM AtencionMedica a)")
    int countCompletadasSinAtencion();

    int countByEstado(String estado);
    
}
