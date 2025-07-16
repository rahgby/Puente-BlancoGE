package com.puenteblanco.pb.services.impl;

import com.puenteblanco.pb.entity.RecoveryToken;
import com.puenteblanco.pb.entity.User;
import com.puenteblanco.pb.repository.RecoveryTokenRepository;
import com.puenteblanco.pb.repository.UserRepository;
import com.puenteblanco.pb.services.interfaces.RecoveryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecoveryServiceImpl implements RecoveryService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final RecoveryTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void enviarCodigoRecuperacion(String correo) {
        User user = userRepository.findClientByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("El correo no pertenece a ningún cliente registrado"));

        String codigo = generarCodigo();
        RecoveryToken token = RecoveryToken.builder()
                .correo(correo)
                .codigo(codigo)
                .fechaCreacion(LocalDateTime.now())
                .usado(false)
                .build();

        tokenRepository.save(token);
        enviarCorreo(correo, codigo);
    }

    @Transactional
    @Override
    public void cambiarContrasena(String correo, String codigo, String nuevaContrasena) {
        RecoveryToken token = tokenRepository.findByCorreoAndCodigoAndUsadoFalse(correo, codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido o expirado"));

        if (token.getFechaCreacion().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código ha expirado, solicita uno nuevo.");
        }

        User user = userRepository.findClientByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("El correo no pertenece a ningún cliente registrado"));

        user.setContrasena(passwordEncoder.encode(nuevaContrasena));
        userRepository.save(user);

        token.setUsado(true);
        tokenRepository.save(token);
    }

    private String generarCodigo() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void enviarCorreo(String correo, String codigo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(correo);
            helper.setSubject("Recuperación de contraseña - Clínica Puente Blanco");
            helper.setText("<p>Tu código de recuperación es: <b>" + codigo + "</b></p>", true);
            mailSender.send(message);
            System.out.println("✅ Correo enviado a: " + correo);
        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
