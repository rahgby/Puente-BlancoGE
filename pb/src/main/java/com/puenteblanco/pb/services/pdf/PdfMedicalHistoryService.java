package com.puenteblanco.pb.services.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.puenteblanco.pb.dto.reportes.HistorialMedicoMascotaDTO;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfMedicalHistoryService {

    public void generarPdfHistorialMedico(OutputStream outputStream, List<HistorialMedicoMascotaDTO> datos, String emitidoPor) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.DARK_GRAY);

            // Logo
            try {
                Image logo = Image.getInstance("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRUmPimESLrfakGu3D6-CudXDLQiIGow6GElg&s");
                logo.scaleToFit(80, 80);
                logo.setAlignment(Image.LEFT | Image.TEXTWRAP);
                document.add(logo);
            } catch (Exception ignored) {}

            // Encabezado
            Paragraph p1 = new Paragraph("Clínica y Farmacia Veterinaria Puente Blanco - CLIFARVET", titleFont);
            p1.setAlignment(Element.ALIGN_CENTER);
            document.add(p1);

            Paragraph p2 = new Paragraph("Urb. Puente Blanco J-9 Ica, Ica, Perú", infoFont);
            p2.setAlignment(Element.ALIGN_CENTER);
            document.add(p2);

            Paragraph p3 = new Paragraph("RUC: 12345678901", infoFont);
            p3.setAlignment(Element.ALIGN_CENTER);
            document.add(p3);

            document.add(Chunk.NEWLINE);

            Paragraph titulo = new Paragraph("HISTORIAL MÉDICO DE MASCOTA", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            if (!datos.isEmpty()) {
                HistorialMedicoMascotaDTO info = datos.get(0);
                Paragraph p4 = new Paragraph("Nombre: " + info.getNombreMascota(), cellFont);
                Paragraph p5 = new Paragraph("Tipo: " + info.getTipoMascota() + "     Raza: " + info.getRaza(), cellFont);
                Paragraph p6 = new Paragraph("Propietario: " + info.getPropietario(), cellFont);
                p4.setSpacingAfter(3f);
                p5.setSpacingAfter(3f);
                p6.setSpacingAfter(10f);
                document.add(p4);
                document.add(p5);
                document.add(p6);
            }

            // Tabla
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2f, 2.5f, 2.5f, 2.5f});
            String[] headers = {"Fecha", "Servicio", "Diagnóstico", "Tratamiento", "Observaciones"};

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(BaseColor.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (HistorialMedicoMascotaDTO dto : datos) {
                table.addCell(new PdfPCell(new Phrase(dto.getFecha().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getServicio(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getDiagnostico(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getTratamiento(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getObservaciones(), cellFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Footer
            Paragraph pie = new Paragraph("Reporte generado automáticamente por el sistema", noteFont);
            pie.setAlignment(Element.ALIGN_RIGHT);
            document.add(pie);

            Paragraph emitido = new Paragraph("Emitido por: " + emitidoPor + " - Fecha: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), noteFont);
            emitido.setAlignment(Element.ALIGN_RIGHT);
            document.add(emitido);

            Paragraph hora = new Paragraph("Hora: " + java.time.LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")), noteFont);
            hora.setAlignment(Element.ALIGN_RIGHT);
            document.add(hora);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de historial médico", e);
        }
    }
}
