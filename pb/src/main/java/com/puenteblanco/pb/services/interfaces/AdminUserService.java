package com.puenteblanco.pb.services.interfaces;

import com.puenteblanco.pb.dto.request.AdminCreateUserRequestDto;
import com.puenteblanco.pb.dto.request.AdminUpdateUserRequestDto;
import com.puenteblanco.pb.dto.response.AdminUserResponseDto;

import java.util.List;

public interface AdminUserService {
    List<AdminUserResponseDto> listarUsuariosPorRol(String rol);
    
    void toggleEstadoUsuario(Long id);

    void crearUsuario(AdminCreateUserRequestDto dto);

    void actualizarUsuario(Long id, AdminUpdateUserRequestDto dto);
}
