package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.entity.Cita;
import com.puenteblanco.pb.entity.User;
import com.puenteblanco.pb.exception.ResourceNotFoundException;
import com.puenteblanco.pb.repository.CitaRepository;
import com.puenteblanco.pb.repository.UserRepository;
import com.puenteblanco.pb.services.interfaces.VetAssignInternService;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class VetAssignInternServiceImpl implements VetAssignInternService {

    private final CitaRepository citaRepository;
    private final UserRepository userRepository;

    public VetAssignInternServiceImpl(CitaRepository citaRepository, UserRepository userRepository) {
        this.citaRepository = citaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void assignInternToAppointment(Long citaId, Long internId, String correoVeterinario) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        String correoAsignado = Optional.ofNullable(cita.getVeterinario())
        .map(v -> v.getUsuario())
        .map(User::getCorreo)
        .orElse(null);

if (correoAsignado == null || !correoAsignado.equalsIgnoreCase(correoVeterinario)) {
    throw new ResourceNotFoundException("No puedes asignar una cita que no te pertenece");
}

        User intern = userRepository.findById(internId)
                .orElseThrow(() -> new ResourceNotFoundException("Practicante no encontrado"));

        if (!intern.getRole().getId().equals(4L)) {
    throw new IllegalArgumentException("El usuario no es un practicante válido");
}

        cita.setIntern(intern);
        cita.setEstado("DERIVADA");
        citaRepository.save(cita);
    }
}
