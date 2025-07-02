// InternAppointmentServiceImpl.java
package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.dto.response.InternAppointmentResponseDto;
import com.puenteblanco.pb.dto.response.InternCitaValidadaResponseDto;
import com.puenteblanco.pb.entity.Cita;
import com.puenteblanco.pb.repository.CitaRepository;
import com.puenteblanco.pb.services.interfaces.InternAppointmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class InternAppointmentServiceImpl implements InternAppointmentService {

    private final CitaRepository citaRepository;

    public InternAppointmentServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public List<InternAppointmentResponseDto> getCitasDerivadasDelInterno(Long internId) {
        List<Cita> citas = citaRepository.findByIntern_IdAndEstado(internId, "DERIVADA");

        return citas.stream().map(cita -> InternAppointmentResponseDto.builder()
                .citaId(cita.getId())

                // Datos del cliente
                .nombreCliente(cita.getUsuario().getNombres() + " " +
                               cita.getUsuario().getApellidoPaterno() + " " +
                               cita.getUsuario().getApellidoMaterno())

                // Datos de la mascota
                .nombreMascota(cita.getPet().getName())
                .tipoMascota(cita.getPet().getType())
                .razaMascota(cita.getPet().getBreed())
                .edadMascota(cita.getPet().getAge())

                // Servicio
                .servicio(cita.getServicio().getDescripcion())
                .fecha(cita.getFecha().toString())
                .hora(cita.getHora().toString())

                // Veterinario a cargo
                .veterinarioACargo(cita.getVeterinario().getUsuario().getNombres() + " " +
                                   cita.getVeterinario().getUsuario().getApellidoPaterno())

                .estado(cita.getEstado())
                .build()
        ).collect(toList());
    }

    @Override
    public List<InternCitaValidadaResponseDto> getCitasValidadasPorIntern(Long internId) {
        List<Cita> citas = citaRepository.findCitasValidadasPorIntern(internId);

        return citas.stream().map(cita -> new InternCitaValidadaResponseDto(
                cita.getFecha().toString() + " " + cita.getHora().toString(),
                cita.getPet().getName(),
                cita.getServicio().getDescripcion(),
                cita.getVeterinario().getUsuario().getNombres() + " " + cita.getVeterinario().getUsuario().getApellidoPaterno(),
                cita.getEstado(),
                cita.getUsuario().getNombres() + " " + cita.getUsuario().getApellidoPaterno()
        )).collect(Collectors.toList());
    }
}
