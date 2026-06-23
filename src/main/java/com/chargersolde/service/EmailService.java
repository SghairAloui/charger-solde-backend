package com.chargersolde.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envoie les credentials de connexion au nouveau client
     */
    public void sendCredentialsEmail(String toEmail, String nom, String prenom,
                                      String email, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenue sur Charger Solde - Vos identifiants de connexion");

            String htmlContent = buildCredentialsEmailHtml(nom, prenom, email, password);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de credentials envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email de credentials à {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email: " + e.getMessage());
        }
    }

    /**
     * Envoie le lien de réinitialisation du mot de passe
     */
    public void sendResetPasswordEmail(String toEmail, String nom, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Charger Solde - Réinitialisation de votre mot de passe");

            String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
            String htmlContent = buildResetPasswordEmailHtml(nom, resetLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de reset password envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email de reset à {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email: " + e.getMessage());
        }
    }

    // ===== Templates HTML =====

    private String buildCredentialsEmailHtml(String nom, String prenom,
                                              String email, String password) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background-color: #fff;
                                 padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background-color: #1a73e8; color: white; padding: 20px;
                              text-align: center; border-radius: 8px 8px 0 0; margin: -30px -30px 30px; }
                    .credentials-box { background-color: #f8f9fa; border: 1px solid #dee2e6;
                                       border-radius: 6px; padding: 20px; margin: 20px 0; }
                    .credential-item { margin: 10px 0; }
                    .label { font-weight: bold; color: #555; }
                    .value { color: #1a73e8; font-size: 16px; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffc107;
                               border-radius: 4px; padding: 15px; margin: 20px 0; }
                    .btn { display: inline-block; background-color: #1a73e8; color: white;
                           padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; color: #888; font-size: 12px; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>💳 Charger Solde</h1>
                        <p>Bienvenue sur notre plateforme</p>
                    </div>
                    
                    <h2>Bonjour %s %s,</h2>
                    <p>Votre compte a été créé avec succès. Voici vos identifiants de connexion :</p>
                    
                    <div class="credentials-box">
                        <div class="credential-item">
                            <span class="label">📧 Email (login) :</span><br>
                            <span class="value">%s</span>
                        </div>
                        <div class="credential-item">
                            <span class="label">🔒 Mot de passe :</span><br>
                            <span class="value">%s</span>
                        </div>
                    </div>
                    
                    <div class="warning">
                        ⚠️ <strong>Important :</strong> Nous vous recommandons de changer votre mot de passe
                        après votre première connexion pour des raisons de sécurité.
                    </div>
                    
                    <center>
                        <a href="http://localhost:4200/login" class="btn">Se connecter</a>
                    </center>
                    
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement. Ne pas répondre à cet email.</p>
                        <p>© 2024 Charger Solde. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(prenom, nom, email, password);
    }

    private String buildResetPasswordEmailHtml(String nom, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background-color: #fff;
                                 padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background-color: #dc3545; color: white; padding: 20px;
                              text-align: center; border-radius: 8px 8px 0 0; margin: -30px -30px 30px; }
                    .btn { display: inline-block; background-color: #dc3545; color: white;
                           padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffc107;
                               border-radius: 4px; padding: 15px; margin: 20px 0; }
                    .footer { text-align: center; color: #888; font-size: 12px; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Charger Solde</h1>
                        <p>Réinitialisation du mot de passe</p>
                    </div>
                    
                    <h2>Bonjour %s,</h2>
                    <p>Vous avez demandé la réinitialisation de votre mot de passe.</p>
                    <p>Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>
                    
                    <center>
                        <a href="%s" class="btn">Réinitialiser mon mot de passe</a>
                    </center>
                    
                    <div class="warning">
                        ⚠️ Ce lien est valable pendant <strong>1 heure</strong> seulement.
                        Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.
                    </div>
                    
                    <p>Si le bouton ne fonctionne pas, copiez ce lien dans votre navigateur :</p>
                    <p style="color: #1a73e8; word-break: break-all;">%s</p>
                    
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement. Ne pas répondre à cet email.</p>
                        <p>© 2024 Charger Solde. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nom, resetLink, resetLink);
    }
}
