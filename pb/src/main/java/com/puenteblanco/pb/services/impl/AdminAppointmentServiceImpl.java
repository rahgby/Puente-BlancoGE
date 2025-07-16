package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.dto.request.AdminUpdateAppointmentRequestDto;
import com.puenteblanco.pb.dto.response.AdminAppointmentResponseDto;
import com.puenteblanco.pb.entity.Cita;
import com.puenteblanco.pb.entity.Pet;
import com.puenteblanco.pb.entity.Servicio;
import com.puenteblanco.pb.entity.User;
import com.puenteblanco.pb.repository.CitaRepository;
import com.puenteblanco.pb.repository.PetRepository;
import com.puenteblanco.pb.repository.ServiceRepository;
import com.puenteblanco.pb.repository.UserRepository;
import com.puenteblanco.pb.services.interfaces.AdminAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAppointmentServiceImpl implements AdminAppointmentService {

    private final CitaRepository citaRepository;
    private final PetRepository petRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Override
    public List<AdminAppointmentResponseDto> listarCitas() {
        return citaRepository.findAll().stream().map(cita -> {
            AdminAppointmentResponseDto dto = new AdminAppointmentResponseDto();
            dto.setId(cita.getId());
            dto.setFecha(cita.getFecha().toString());
            dto.setHora(cita.getHora().toString());
            dto.setCliente(cita.getUsuario().getNombres() + " " + cita.getUsuario().getApellidoPaterno());
            dto.setMascota(cita.getPet().getName() + " (" + cita.getPet().getType() + ")");
            dto.setServicio(cita.getServicio().getDescripcion());
            dto.setDoctor(cita.getVeterinario() != null
                    ? cita.getVeterinario().getUsuario().getNombres() + " " + cita.getVeterinario().getUsuario().getApellidoPaterno()
                    : "-");
            dto.setEstadoCita(cita.getEstado()); // PROGRAMADA, COMPLETADA, etc.
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void actualizarCita(Long id, AdminUpdateAppointmentRequestDto dto) {
        Cita cita = citaRepository.findById(id).orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        User doctor = userRepository.findById(dto.getVeterinarioId()).orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        Servicio servicio = serviceRepository.findById(dto.getServicioId()).orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        cita.setUsuario(doctor);
        cita.setServicio(servicio);

        citaRepository.save(cita);
    }
}
