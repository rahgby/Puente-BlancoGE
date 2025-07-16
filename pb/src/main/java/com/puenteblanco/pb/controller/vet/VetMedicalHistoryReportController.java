package com.puenteblanco.pb.controller.vet;

import com.puenteblanco.pb.dto.reportes.HistorialMedicoMascotaDTO;
import com.puenteblanco.pb.services.interfaces.VetMedicalHistoryReportService;
import com.puenteblanco.pb.services.pdf.PdfMedicalHistoryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/vet/history")
@RequiredArgsConstructor
public class VetMedicalHistoryReportController {

    private final VetMedicalHistoryReportService vetMedicalHistoryReportService;
    private final PdfMedicalHistoryService pdfMedicalHistoryService;

    @GetMapping("/{petId}/download")
    public void descargarHistorialMedicoPdf(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response
    ) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=historial_medico_mascota.pdf");

            List<HistorialMedicoMascotaDTO> datos = vetMedicalHistoryReportService.obtenerHistorialMedico(petId);
            String emitidoPor = userDetails.getUsername();

            OutputStream out = response.getOutputStream();
            pdfMedicalHistoryService.generarPdfHistorialMedico(out, datos, emitidoPor);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar historial médico PDF", e);
        }
    }
}
