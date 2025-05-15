package com.example.demo;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.Style;
import java.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConstanciaService {

    private final SolicitudConstanciaRepository repository;
    private final JavaMailSender mailSender;

    public SolicitudConstancia registrarSolicitud(SolicitudConstanciaDTO dto) {
        SolicitudConstancia solicitud = new SolicitudConstancia();
        solicitud.setNombre(dto.getNombre());
        solicitud.setMatricula(dto.getMatricula());
        solicitud.setCorreo(dto.getCorreo());
        solicitud.setTipoConstancia(dto.getTipoConstancia());
        
        solicitud = repository.save(solicitud);
        
        enviarConstanciaPorCorreo(solicitud);
        
        return solicitud;
    }

    public List<SolicitudConstancia> obtenerTodasLasSolicitudes() {
        return repository.findAll();
    }

    private void enviarConstanciaPorCorreo(SolicitudConstancia solicitud) {
        try {
            byte[] pdfBytes = generarPDF(solicitud);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(solicitud.getCorreo());
            helper.setSubject("Constancia Académica - " + solicitud.getTipoConstancia());
            
            String htmlContent = """
                <div style="max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif; padding: 20px;">
                    <div style="background-color: #003366; padding: 20px; text-align: center;">
                        <h1 style="color: white; margin: 0;">Constancia Académica</h1>
                    </div>
                    
                    <div style="padding: 20px; background-color: #ffffff; border: 1px solid #e0e0e0;">
                        <p style="font-size: 16px; color: #333;">Estimado/a <strong>%s</strong>,</p>
                        
                        <p style="font-size: 14px; color: #555; line-height: 1.5;">
                            Nos complace informarle que su solicitud de constancia académica ha sido procesada exitosamente.
                            Adjunto a este correo encontrará el documento PDF con su constancia.
                        </p>
                        
                        <div style="background-color: #f5f5f5; padding: 15px; margin: 20px 0; border-left: 4px solid #003366;">
                            <p style="margin: 0; font-size: 14px;">
                                <strong>Detalles de la solicitud:</strong><br>
                                Tipo de Constancia: %s<br>
                                Matrícula: %s<br>
                                Fecha de Solicitud: %s
                            </p>
                        </div>
                        
                        <p style="font-size: 14px; color: #555;">
                            Si tiene alguna pregunta o necesita asistencia adicional, no dude en contactarnos.
                        </p>
                    </div>
                    
                    <div style="text-align: center; padding: 20px; background-color: #f5f5f5;">
                        <p style="color: #666; font-size: 12px; margin: 0;">
                            Este es un correo automático, por favor no responda a este mensaje.<br>
                            © 2024 Institución Educativa. Todos los derechos reservados.
                        </p>
                    </div>
                </div>
            """.formatted(
                solicitud.getNombre(),
                solicitud.getTipoConstancia(),
                solicitud.getMatricula(),
                solicitud.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            
            helper.setText(htmlContent, true);
            helper.addAttachment("constancia_" + solicitud.getMatricula() + ".pdf", new ByteArrayResource(pdfBytes));
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }
    
    private byte[] generarPDF(SolicitudConstancia solicitud) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
    
            // Configuración de fuentes
            PdfFont fontTitulo = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
    
            // Agregar logo (opcional)
            // Image logo = new Image(ImageDataFactory.create("ruta/al/logo.png"));
            // logo.setWidth(100);
            // document.add(logo);
    
            // Título principal
            Paragraph titulo = new Paragraph("CONSTANCIA ACADÉMICA")
                .setFont(fontTitulo)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
            document.add(titulo);
    
            // Fecha
            Paragraph fecha = new Paragraph(
                "Fecha de emisión: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")))
                .setFont(fontNormal)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(30);
            document.add(fecha);
    
            // Contenido principal
            document.add(new Paragraph("Por medio de la presente se hace constar que:")
                .setFont(fontNormal)
                .setFontSize(12)
                .setMarginBottom(20));
    
            // Tabla de datos
            Table tabla = new Table(new float[]{150, 350});
            tabla.setWidth(UnitValue.createPercentValue(100));
            
            // Estilo para las celdas
            Style headerStyle = new Style()
                .setBackgroundColor(new DeviceRgb(0, 51, 102))
                .setFontColor(ColorConstants.WHITE)
                .setFont(fontTitulo)
                .setFontSize(12);
    
            // Agregar filas a la tabla
            agregarFilaTabla(tabla, "Nombre:", solicitud.getNombre(), headerStyle, fontNormal);
            agregarFilaTabla(tabla, "Matrícula:", solicitud.getMatricula(), headerStyle, fontNormal);
            agregarFilaTabla(tabla, "Tipo de Constancia:", solicitud.getTipoConstancia(), headerStyle, fontNormal);
            agregarFilaTabla(tabla, "Fecha de Solicitud:", 
                solicitud.getFechaSolicitud().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 
                headerStyle, fontNormal);
    
            document.add(tabla);
    
            // Pie de página
            Paragraph footer = new Paragraph(
                "\n\nEste documento es una constancia oficial emitida por la Institución Educativa. " +
                "Para verificar la autenticidad de este documento, por favor contacte a la oficina de registro académico.")
                .setFont(fontNormal)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
            document.add(footer);
    
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
    
    private void agregarFilaTabla(Table tabla, String etiqueta, String valor, Style headerStyle, PdfFont fontNormal) {
        Cell celdaEtiqueta = new Cell()
            .add(new Paragraph(etiqueta))
            .addStyle(headerStyle)
            .setPadding(5);
        
        Cell celdaValor = new Cell()
            .add(new Paragraph(valor)
                .setFont(fontNormal)
                .setFontSize(12))
            .setPadding(5);
        
        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
}
