package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.dto.request.AdminCreateUserRequestDto;
import com.puenteblanco.pb.dto.request.AdminUpdateUserRequestDto;
import com.puenteblanco.pb.dto.response.AdminUserResponseDto;
import com.puenteblanco.pb.entity.Role;
import com.puenteblanco.pb.entity.TipoDocumento;
import com.puenteblanco.pb.entity.User;
import com.puenteblanco.pb.exception.ResourceNotFoundException;
import com.puenteblanco.pb.repository.UserRepository;
import com.puenteblanco.pb.repository.RoleRepository;
import com.puenteblanco.pb.repository.TipoDocumentoRepository;
import com.puenteblanco.pb.services.interfaces.AdminUserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
public List<AdminUserResponseDto> listarUsuariosPorRol(String rolNombre) {
    Long roleId = null;
    if (rolNombre != null && !rolNombre.equalsIgnoreCase("ALL")) {
        Optional<Role> role = roleRepository.findByNombre(rolNombre.toUpperCase());
        if (role.isPresent()) {
            roleId = role.get().getId();
        } else {
            return List.of(); // Si no se encuentra el rol, retornamos lista vacía
        }
    }

    return userRepository.findAllByOptionalRoleId(roleId).stream()
            .map(user -> new AdminUserResponseDto(
                user.getId(),
                user.getNombres(),
                user.getCorreo(),
                user.getRole().getNombre(),
                user.getRole().getId().intValue(),
                user.getEstado()
            ))
            .collect(Collectors.toList());
}

    @Override
    public void toggleEstadoUsuario(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setEstado(!Boolean.TRUE.equals(user.getEstado()));
        userRepository.save(user);
    }


    @Override
@Transactional
public void crearUsuario(AdminCreateUserRequestDto dto) {
    if (userRepository.existsByCorreo(dto.getCorreo())) {
        throw new IllegalArgumentException("El correo ya está registrado");
    }

    Role rol = roleRepository.findById(dto.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));

    TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getTipoDocumentoId())
            .orElseThrow(() -> new IllegalArgumentException("Tipo de documento inválido"));

    User user = new User();
    user.setNombres(dto.getNombres());
    user.setApellidoPaterno(dto.getApellidoPaterno());
    user.setApellidoMaterno(dto.getApellidoMaterno());
    user.setNumeroIdentidad(dto.getNumeroIdentidad());
    user.setTipoDocumento(tipoDoc);
    user.setTelefono(dto.getTelefono());
    user.setFechaNacimiento(dto.getFechaNacimiento());
    user.setSexo(dto.getSexo());
    user.setDireccion(dto.getDireccion());
    user.setCorreo(dto.getCorreo());
    user.setRole(rol);
    user.setEstado(true);
    user.setContrasena(passwordEncoder.encode(dto.getContrasena())); // Contraseña por defecto

    userRepository.save(user);
}

@Override
    public void actualizarUsuario(Long id, AdminUpdateUserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        user.setNombres(dto.getNombre());

        if (dto.getRoleId() != null) {
            Role nuevoRol = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
            user.setRole(nuevoRol);
        }

        userRepository.save(user);
    }

}
