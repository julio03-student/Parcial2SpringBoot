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
        
        //enviarConstanciaPorCorreo(solicitud);
        
        return solicitud;
    }

    public List<SolicitudConstancia> obtenerTodasLasSolicitudes() {
        return repository.findAll();
    }

    private void enviarConstanciaPorCorreo(SolicitudConstancia solicitud) {
        try {
            byte[] pdfBytes = generarPDF(solicitud);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(solicitud.getCorreo());
            helper.setSubject("Constancia Académica - " + solicitud.getTipoConstancia());
            helper.setText("Estimado/a " + solicitud.getNombre() + ",\n\n" +
                         "Adjunto encontrará su constancia académica solicitada.\n\n" +
                         "Saludos cordiales,\nInstitución Educativa");
            
            helper.addAttachment("constancia.pdf", new ByteArrayResource(pdfBytes));
            
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

            document.add(new Paragraph("CONSTANCIA ACADÉMICA"));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Por medio de la presente se hace constar que:"));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Nombre: " + solicitud.getNombre()));
            document.add(new Paragraph("Matrícula: " + solicitud.getMatricula()));
            document.add(new Paragraph("Tipo de Constancia: " + solicitud.getTipoConstancia()));
            
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}
