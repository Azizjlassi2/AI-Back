package com.aiplus.backend.email.strategy;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.aiplus.backend.email.exceptions.PasswordResetEmailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Strategy for sending password reset emails.
 */
@Component
public class PasswordResetEmailStrategy implements EmailStrategy {

    private final JavaMailSender mailSender;

    public PasswordResetEmailStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(EmailType type) {
        return type == EmailType.PASSWORD_RESET;
    }

    @Override
    public void sendEmail(String to, String resetUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            String emailContent = """
                    <h2>Bonjour %s,</h2>

                    <p>Nous avons reçu une demande de réinitialisation de votre mot de passe sur AI+. Si vous en êtes l'auteur, suivez les étapes ci-dessous pour sécuriser votre compte.</p>

                    <p><strong>Action requise :</strong> Cliquez sur le bouton ci-dessous pour définir un nouveau mot de passe. Ce lien expire dans 24 heures pour votre sécurité.</p>

                    <a href="%s" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Réinitialiser mon mot de passe</a>

                    <p><em>Remarque importante :</em> Si vous n'avez pas initié cette demande, ignorez simplement cet e-mail. Votre compte reste protégé, et aucune action n'est nécessaire de votre part.</p>

                    <p>Si vous rencontrez des difficultés, contactez notre support à <a href="mailto:support@aiplus.com">support@aiplus.com</a>.</p>

                    <p>Cordialement,<br>L'équipe AI+</p>

                    <p style="font-size: 12px; color: #666;"><a href="${unsubscribeUrl}">Se désabonner</a> | AI+ - Confidentialité</p>
                    """;
            helper.setText(String.format(emailContent, to, resetUrl), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new PasswordResetEmailException("Failed to send password reset email", e);
        }
    }
}
