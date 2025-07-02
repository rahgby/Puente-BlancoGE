package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.dto.request.AdminCreateServiceRequestDto;
import com.puenteblanco.pb.dto.response.AdminServiceResponseDto;
import com.puenteblanco.pb.entity.Servicio;
import com.puenteblanco.pb.entity.TipoServicio;
import com.puenteblanco.pb.exception.ResourceNotFoundException;
import com.puenteblanco.pb.repository.ServiceRepository;
import com.puenteblanco.pb.repository.TipoServicioRepository;
import com.puenteblanco.pb.services.interfaces.AdminServiceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceServiceImpl implements AdminServiceService {

    private final ServiceRepository serviceRepository;
    private final TipoServicioRepository tipoServicioRepository;

    @Override
    public void registrarServicio(AdminCreateServiceRequestDto dto) {
        TipoServicio tipo = tipoServicioRepository.findById(dto.getTipoServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado"));

        Servicio servicio = new Servicio();
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecioBase(BigDecimal.valueOf(dto.getPrecioBase()));
        servicio.setTipoServicio(tipo);
        servicio.setEstado(true); // Activo por defecto

        serviceRepository.save(servicio);
    }

    @Override
    public List<AdminServiceResponseDto> listarServicios() {
        return serviceRepository.findAll().stream().map(servicio -> {
            AdminServiceResponseDto dto = new AdminServiceResponseDto();
            dto.setId(servicio.getId());
            dto.setNombre(servicio.getDescripcion());
            dto.setPrecio(servicio.getPrecioBase());
            dto.setEstado(servicio.getEstado());
            dto.setCategoria(servicio.getTipoServicio().getNombre());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
@Transactional
public void editarServicio(Long id, AdminCreateServiceRequestDto dto) {
    Servicio servicio = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    TipoServicio tipo = tipoServicioRepository.findById(dto.getTipoServicioId())
            .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio no encontrado"));

    servicio.setDescripcion(dto.getDescripcion()); // El campo 'descripcion' representa el nombre
    servicio.setPrecioBase(BigDecimal.valueOf(dto.getPrecioBase()));
    servicio.setTipoServicio(tipo);

    serviceRepository.save(servicio);
}

@Override
@Transactional
public void cambiarEstadoServicio(Long id) {
    Servicio servicio = serviceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    servicio.setEstado(!servicio.getEstado()); // Cambia de activo a inactivo o viceversa
    serviceRepository.save(servicio);
}

@Override
public void eliminarServicio(Long id) {
    Servicio servicio = serviceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
    serviceRepository.delete(servicio);
}
}