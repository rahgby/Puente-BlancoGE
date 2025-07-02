package com.puenteblanco.pb.services.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.puenteblanco.pb.dto.reportes.CitaCanceladaDTO;
import com.puenteblanco.pb.dto.reportes.CitaPorFechaDTO;
import com.puenteblanco.pb.dto.reportes.CitaPorMascotaDTO;
import com.puenteblanco.pb.config.ChartGeneratorConfig;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;

@Service
public class PdfReportService {

    public void generarPdfCitasPorFecha(
        LocalDate startDate,
        LocalDate endDate,
        OutputStream outputStream,
        List<CitaPorFechaDTO> datos,
        String emitidoPor,
        String tipoServicio
) {
    try {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.DARK_GRAY);

        // Añadir logo
        try {
            Image logo = Image.getInstance("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRUmPimESLrfakGu3D6-CudXDLQiIGow6GElg&s");
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.LEFT | Image.TEXTWRAP);
            document.add(logo);
        } catch (Exception e) {
            // Continuar si falla el logo
        }

        // Encabezado
        Paragraph header = new Paragraph("Clínica y Farmacia Veterinaria Puente Blanco - CLIFARVET", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph dir = new Paragraph("Urb. Puente Blanco J-9 Ica, Ica, Perú", infoFont);
        dir.setAlignment(Element.ALIGN_CENTER);
        document.add(dir);

        Paragraph ruc = new Paragraph("RUC: 12345678901", infoFont);
        ruc.setAlignment(Element.ALIGN_CENTER);
        document.add(ruc);

        document.add(Chunk.NEWLINE);

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE CITAS POR SERVICIO", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(Chunk.NEWLINE);
        Paragraph rangoFechas = new Paragraph("Rango de fechas: De " + startDate + " a " + endDate, cellFont);
        rangoFechas.setAlignment(Element.ALIGN_LEFT);
        rangoFechas.setSpacingAfter(10);
        document.add(rangoFechas);

        // Tabla
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2, 2, 2});

        String[] headers = {"Fecha", "Hora", "Cliente", "Mascota", "Servicio", "Estado"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (CitaPorFechaDTO dto : datos) {
            table.addCell(new PdfPCell(new Phrase(dto.getFecha().toString(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getHora().toString(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getCliente(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getMascota(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getServicio(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getEstado(), cellFont)));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Gráfico
        try {
            Map<String, Long> agrupado;

            if (tipoServicio == null || tipoServicio.isBlank()) {
                agrupado = datos.stream()
                        .collect(Collectors.groupingBy(CitaPorFechaDTO::getServicio, Collectors.counting()));

                BufferedImage grafico = ChartGeneratorConfig.generarGraficoBarras(
                        agrupado, "Servicios Realizados", "Servicio", "Cantidad");

                Image chartImage = Image.getInstance(grafico, null);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                chartImage.scaleToFit(450, 280);
                document.add(chartImage);

            } else {
                agrupado = datos.stream()
                        .collect(Collectors.groupingBy(dto -> dto.getFecha().toString(), Collectors.counting()));

                BufferedImage grafico = ChartGeneratorConfig.generarGraficoLineas(
                        agrupado, "Citas por Día", "Fecha", "Cantidad");

                Image chartImage = Image.getInstance(grafico, null);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                chartImage.scaleToFit(450, 280);
                document.add(chartImage);
            }
        } catch (Exception ex) {
            System.err.println("Error al generar el gráfico: " + ex.getMessage());
        }

        document.add(Chunk.NEWLINE);

        // Footer
        Paragraph nota = new Paragraph("Reporte generado automáticamente por el sistema", noteFont);
        nota.setAlignment(Element.ALIGN_RIGHT);
        document.add(nota);

        Paragraph footer = new Paragraph("Emitido por: " + emitidoPor + " - Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), noteFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        Paragraph hora = new Paragraph("Hora: " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")), noteFont);
        hora.setAlignment(Element.ALIGN_RIGHT);
        document.add(hora);

        document.close();
    } catch (Exception e) {
        throw new RuntimeException("Error generando PDF de citas por fecha", e);
    }
}

    public void generarPdfCitasPorMascota(
        LocalDate startDate,
        LocalDate endDate,
        OutputStream outputStream,
        List<CitaPorMascotaDTO> datos,
        String emitidoPor
) {
    try {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.DARK_GRAY);

        // Añadir logo
        try {
            Image logo = Image.getInstance("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRUmPimESLrfakGu3D6-CudXDLQiIGow6GElg&s");
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.LEFT | Image.TEXTWRAP);
            document.add(logo);
        } catch (Exception e) {
            // Continuar si falla el logo
        }

        Paragraph header = new Paragraph("Clínica y Farmacia Veterinaria Puente Blanco - CLIFARVET", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph dir = new Paragraph("Urb. Puente Blanco J-9 Ica, Ica, Perú", infoFont);
        dir.setAlignment(Element.ALIGN_CENTER);
        document.add(dir);

        Paragraph ruc = new Paragraph("RUC: 12345678901", infoFont);
        ruc.setAlignment(Element.ALIGN_CENTER);
        document.add(ruc);

        document.add(Chunk.NEWLINE);

        Paragraph titulo = new Paragraph("REPORTE DE CITAS POR MASCOTA", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(Chunk.NEWLINE);

        Paragraph rangoFechas = new Paragraph("Rango de fechas: " + startDate + " a " + endDate, cellFont);
        rangoFechas.setAlignment(Element.ALIGN_LEFT);
        rangoFechas.setSpacingAfter(10);
        document.add(rangoFechas);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 2f, 2.5f, 2.5f, 2.5f, 2f, 2f});

        String[] headers = {"Mascota", "Tipo", "Raza", "Servicio", "Cliente", "Fecha", "Hora"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (CitaPorMascotaDTO dto : datos) {
            table.addCell(new PdfPCell(new Phrase(dto.getNombreMascota(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getTipoMascota(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getRaza(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getServicio(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getCliente(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getFecha().toString(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(dto.getHora().toString(), cellFont)));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Gráfico adaptativo según diversidad de tipos de mascota
try {
    Map<String, Long> agrupado;

    Set<String> tiposMascotaUnicos = datos.stream()
        .map(CitaPorMascotaDTO::getTipoMascota)
        .collect(Collectors.toSet());

    if (tiposMascotaUnicos.size() > 1) {
        // Si hay varios tipos, mostrar gráfico por tipo
        agrupado = datos.stream()
            .collect(Collectors.groupingBy(CitaPorMascotaDTO::getTipoMascota, Collectors.counting()));

        BufferedImage grafico = ChartGeneratorConfig.generarGraficoBarras(
            agrupado,
            "Cantidad de Citas por Tipo de Mascota",
            "Tipo de Mascota",
            "Cantidad"
        );

        Image chartImage = Image.getInstance(grafico, null);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(450, 280);
        document.add(chartImage);
    } else {
        // Si solo hay un tipo (Perro o Gato), mostrar gráfico por servicio
        agrupado = datos.stream()
            .collect(Collectors.groupingBy(CitaPorMascotaDTO::getServicio, Collectors.counting()));

        String titulo2 = "Servicios brindados a " + tiposMascotaUnicos.iterator().next();

        BufferedImage grafico = ChartGeneratorConfig.generarGraficoBarras(
            agrupado,
            titulo2,
            "Servicio",
            "Cantidad"
        );

        Image chartImage = Image.getInstance(grafico, null);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(450, 280);
        document.add(chartImage);
    }
} catch (Exception e) {
    System.err.println("Error al generar gráfico de citas por mascota: " + e.getMessage());
}

        document.add(Chunk.NEWLINE);

        Paragraph nota = new Paragraph("Reporte generado automáticamente por el sistema", noteFont);
        nota.setAlignment(Element.ALIGN_RIGHT);
        document.add(nota);

        Paragraph fecha = new Paragraph("Emitido por: " + emitidoPor + " - Fecha: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), noteFont);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        document.add(fecha);

        Paragraph hora = new Paragraph("Hora: " + java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")), noteFont);
        hora.setAlignment(Element.ALIGN_RIGHT);
        document.add(hora);

        document.close();
    } catch (Exception e) {
        throw new RuntimeException("Error generando PDF de citas por mascota", e);
    }
}


    public void generarPdfCitasCanceladas(
            LocalDate startDate,
            LocalDate endDate,
            OutputStream outputStream,
            List<CitaCanceladaDTO> datos,
            String emitidoPor
    ) {
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
            Paragraph header = new Paragraph("Clínica y Farmacia Veterinaria Puente Blanco - CLIFARVET", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph dir = new Paragraph("Urb. Puente Blanco J-9 Ica, Ica, Perú", infoFont);
            dir.setAlignment(Element.ALIGN_CENTER);
            document.add(dir);

            Paragraph ruc = new Paragraph("RUC: 12345678901", infoFont);
            ruc.setAlignment(Element.ALIGN_CENTER);
            document.add(ruc);

            document.add(Chunk.NEWLINE);

            Paragraph titulo = new Paragraph("REPORTE DE CITAS CANCELADAS", titleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(Chunk.NEWLINE);

            Paragraph rangoFechas = new Paragraph("Rango de fechas: " + startDate + " a " + endDate, cellFont);
            rangoFechas.setAlignment(Element.ALIGN_LEFT);
            rangoFechas.setSpacingAfter(10);
            document.add(rangoFechas);

            // Tabla
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2f, 2.5f, 2.5f, 2.5f, 2f, 3f});

            String[] headers = {"Cliente", "Mascota", "Servicio", "Veterinario", "Fecha", "Hora", "Motivo Cancelación"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(BaseColor.GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (CitaCanceladaDTO dto : datos) {
                table.addCell(new PdfPCell(new Phrase(dto.getCliente(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getMascota(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getServicio(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getVeterinario(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getFecha().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getHora().toString(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(dto.getMotivoCancelacion(), cellFont)));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Gráfico por tipo de servicio
try {
    Map<String, Long> agrupado = datos.stream()
        .collect(Collectors.groupingBy(CitaCanceladaDTO::getServicio, Collectors.counting()));

    BufferedImage grafico = ChartGeneratorConfig.generarGraficoBarras(
        agrupado,
        "Citas Canceladas por Tipo de Servicio",
        "Servicio",
        "Cantidad"
    );

    Image chartImage = Image.getInstance(grafico, null);
    chartImage.setAlignment(Element.ALIGN_CENTER);
    chartImage.scaleToFit(450, 280);
    document.add(chartImage);
} catch (Exception e) {
    System.err.println("Error al generar gráfico de citas canceladas: " + e.getMessage());
}

            // Nota final
            Paragraph nota = new Paragraph("Reporte generado automáticamente por el sistema", noteFont);
            nota.setAlignment(Element.ALIGN_RIGHT);
            document.add(nota);

            Paragraph fecha = new Paragraph("Emitido por: " + emitidoPor + " - Fecha: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), noteFont);
            fecha.setAlignment(Element.ALIGN_RIGHT);
            document.add(fecha);

            Paragraph hora = new Paragraph("Hora: " +
                    java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), noteFont);
            hora.setAlignment(Element.ALIGN_RIGHT);
            document.add(hora);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de citas canceladas", e);
        }
    }
}
