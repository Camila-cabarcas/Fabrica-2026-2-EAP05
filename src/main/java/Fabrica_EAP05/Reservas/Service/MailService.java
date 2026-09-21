package Fabrica_EAP05.Reservas.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailResetPassword(String destinatario, String nombreUsuario, String resetLink) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");

        helper.setTo(destinatario);
        helper.setFrom("no-reply@fabrica-eap05.com");
        helper.setSubject("Establece tu contraseña - Fabrica EAP05");

        String html = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2>¡Hola %s!</h2>
                    <p>Tu cuenta fue registrada correctamente. Para poder iniciar sesión, primero debes establecer tu contraseña.</p>
                    <p>
                        <a href="%s" style="background-color:#2563eb;color:#fff;padding:10px 20px;text-decoration:none;border-radius:4px;">
                            Establecer contraseña
                        </a>
                    </p>
                    <p>Si el botón no funciona, copia y pega este link en tu navegador:</p>
                    <p>%s</p>
                    <p>Este link expira en un tiempo limitado. Si no solicitaste este registro, ignora este correo.</p>
                </body>
                </html>
                """.formatted(nombreUsuario, resetLink, resetLink);

        helper.setText(html, true);
        mailSender.send(mensaje);
    }
}
