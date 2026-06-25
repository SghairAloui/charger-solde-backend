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
     * Envoie le code de réinitialisation du mot de passe
     */
    public void sendResetPasswordEmail(String toEmail, String nom, String resetCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Charge Pay - Code de réinitialisation de votre mot de passe");

            String resetLink = "http://localhost:4200/auth/reset-password?code=" + resetCode + "&email=" + toEmail;
            String htmlContent = buildResetPasswordEmailHtml(nom, resetCode, resetLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de reset password (code) envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email de reset à {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email: " + e.getMessage());
        }
    }

    /**
     * Envoie notification email pour le statut d'une réclamation
     */
    public void sendClaimStatusEmail(String toEmail, String nom, String subject, String status, String response) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Charge Pay - Mise à jour de votre réclamation");

            String htmlContent = buildClaimStatusEmailHtml(nom, subject, status, response);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de statut réclamation envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Erreur envoi email réclamation à {}: {}", toEmail, e.getMessage());
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
                    body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0; }
                    .container { max-width: 580px; margin: 40px auto; background-color: #ffffff;
                                 padding: 40px; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #111827; font-size: 24px; margin: 0 0 5px; font-weight: 800; }
                    .header p { color: #6b7280; font-size: 14px; margin: 0; text-transform: uppercase; letter-spacing: 1px; }
                    .logo-icon { width: 50px; height: 50px; background: linear-gradient(135deg, #6366f1, #4f46e5); color: white; border-radius: 14px; display: inline-flex; align-items: center; justify-content: center; font-size: 24px; margin-bottom: 15px; box-shadow: 0 4px 14px rgba(99, 102, 241, 0.4); line-height: 50px;}
                    h2 { color: #1f2937; font-size: 20px; font-weight: 600; margin-bottom: 15px; }
                    p { color: #4b5563; font-size: 15px; line-height: 1.6; }
                    .credentials-box { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; margin: 25px 0; }
                    .credential-item { margin: 12px 0; display: flex; flex-direction: column; gap: 4px; }
                    .label { font-weight: 600; color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; }
                    .value { color: #0f172a; font-size: 18px; font-family: monospace; font-weight: 700; background: #e2e8f0; padding: 8px 12px; border-radius: 6px; display: inline-block; word-break: break-all; }
                    .warning { background-color: #fffbeb; border-left: 4px solid #f59e0b; padding: 16px; margin: 25px 0; border-radius: 0 8px 8px 0; }
                    .warning p { margin: 0; color: #b45309; font-size: 14px; }
                    .btn-wrap { text-align: center; margin: 35px 0 15px; }
                    .btn { display: inline-block; background: linear-gradient(135deg, #6366f1, #4f46e5); color: #ffffff; padding: 14px 32px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 14px rgba(99, 102, 241, 0.3); transition: all 0.3s ease; }
                    .footer { text-align: center; border-top: 1px solid #f3f4f6; margin-top: 30px; padding-top: 20px; }
                    .footer p { color: #9ca3af; font-size: 12px; margin: 5px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo-icon">⚡</div>
                        <h1>Charge Pay</h1>
                        <p>Bienvenue sur notre plateforme</p>
                    </div>
                    
                    <h2>Bonjour %s %s,</h2>
                    <p>Votre compte client a été créé avec succès. Voici vos identifiants sécurisés pour accéder à votre espace personnel :</p>
                    
                    <div class="credentials-box">
                        <div class="credential-item">
                            <span class="label">Email de connexion</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="credential-item">
                            <span class="label">Mot de passe temporaire</span>
                            <span class="value">%s</span>
                        </div>
                    </div>
                    
                    <div class="warning">
                        <p>⚠️ <strong>Important :</strong> Nous vous recommandons vivement de changer votre mot de passe dès votre première connexion pour garantir la sécurité de votre compte.</p>
                    </div>
                    
                    <div class="btn-wrap">
                        <a href="http://localhost:4200/login" class="btn" style="color: #ffffff;">Accéder à mon compte</a>
                    </div>
                    
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement. Merci de ne pas y répondre.</p>
                        <p>© 2024 Charge Pay. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(prenom, nom, email, password);
    }

    private String buildResetPasswordEmailHtml(String nom, String resetCode, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0; }
                    .container { max-width: 580px; margin: 40px auto; background-color: #ffffff; padding: 40px; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #111827; font-size: 24px; margin: 0 0 5px; font-weight: 800; }
                    .header p { color: #6b7280; font-size: 14px; margin: 0; text-transform: uppercase; letter-spacing: 1px; }
                    .logo-icon { width: 50px; height: 50px; background: linear-gradient(135deg, #6366f1, #4f46e5); color: white; border-radius: 14px; display: inline-flex; align-items: center; justify-content: center; font-size: 24px; margin-bottom: 15px; box-shadow: 0 4px 14px rgba(99, 102, 241, 0.4); line-height: 50px;}
                    h2 { color: #1f2937; font-size: 20px; font-weight: 600; margin-bottom: 15px; }
                    p { color: #4b5563; font-size: 15px; line-height: 1.6; }
                    .code-box { background-color: #f8fafc; border: 2px dashed #6366f1; border-radius: 12px; padding: 24px; margin: 25px 0; text-align: center; }
                    .code-box .code { font-size: 36px; font-weight: 800; letter-spacing: 12px; color: #4f46e5; font-family: monospace; }
                    .code-box .code-label { font-size: 12px; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 8px; }
                    .warning { background-color: #fff1f2; border-left: 4px solid #f43f5e; padding: 16px; margin: 25px 0; border-radius: 0 8px 8px 0; }
                    .warning p { margin: 0; color: #be123c; font-size: 14px; }
                    .btn-wrap { text-align: center; margin: 35px 0; }
                    .btn { display: inline-block; background: linear-gradient(135deg, #6366f1, #4f46e5); color: #ffffff; padding: 14px 32px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 14px rgba(99, 102, 241, 0.3); transition: all 0.3s ease; }
                    .link-fallback { background: #f8fafc; padding: 15px; border-radius: 8px; word-break: break-all; font-size: 13px; color: #6366f1; margin-top: 20px; }
                    .footer { text-align: center; border-top: 1px solid #f3f4f6; margin-top: 30px; padding-top: 20px; }
                    .footer p { color: #9ca3af; font-size: 12px; margin: 5px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo-icon">🔐</div>
                        <h1>Charge Pay</h1>
                        <p>Réinitialisation de mot de passe</p>
                    </div>
                    
                    <h2>Bonjour %s,</h2>
                    <p>Nous avons reçu une demande de réinitialisation du mot de passe pour votre compte.</p>
                    <p>Utilisez le code ci-dessous pour créer un nouveau mot de passe :</p>
                    
                    <div class="code-box">
                        <div class="code-label">Code de vérification</div>
                        <div class="code">%s</div>
                    </div>
                    
                    <p style="text-align: center;">Ou cliquez sur le bouton ci-dessous :</p>
                    
                    <div class="btn-wrap">
                        <a href="%s" class="btn" style="color: #ffffff;">Réinitialiser mon mot de passe</a>
                    </div>
                    
                    <div class="warning">
                        <p>⚠️ Ce code expire dans <strong>1 heure</strong>. Si vous n'avez pas effectué cette demande, vous pouvez ignorer cet email en toute sécurité.</p>
                    </div>
                    
                    <p style="font-size: 13px; color: #6b7280; margin-top: 30px;">Si le bouton ne fonctionne pas, copiez-collez ce lien dans votre navigateur :</p>
                    <div class="link-fallback">%s</div>
                    
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement. Merci de ne pas y répondre.</p>
                        <p>© 2024 Charge Pay. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nom, resetCode, resetLink, resetLink);
    }

    private String buildClaimStatusEmailHtml(String nom, String subject, String status, String response) {
        String statusLabel = switch (status) {
            case "RESOLVED" -> "Résolue";
            case "REJECTED" -> "Rejetée";
            default -> status;
        };
        String statusColor = switch (status) {
            case "RESOLVED" -> "#10b981";
            case "REJECTED" -> "#ef4444";
            default -> "#6b7280";
        };
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; background-color: #f3f4f6; margin: 0; padding: 0; }
                    .container { max-width: 580px; margin: 40px auto; background-color: #ffffff; padding: 40px; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #111827; font-size: 24px; margin: 0 0 5px; font-weight: 800; }
                    .header p { color: #6b7280; font-size: 14px; margin: 0; text-transform: uppercase; letter-spacing: 1px; }
                    .status-badge { display: inline-block; padding: 8px 24px; border-radius: 20px; color: #fff; font-weight: 700; font-size: 16px; background-color: %s; }
                    h2 { color: #1f2937; font-size: 20px; font-weight: 600; margin-bottom: 15px; }
                    p { color: #4b5563; font-size: 15px; line-height: 1.6; }
                    .response-box { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; margin: 25px 0; }
                    .response-box p { margin: 0; }
                    .footer { text-align: center; border-top: 1px solid #f3f4f6; margin-top: 30px; padding-top: 20px; }
                    .footer p { color: #9ca3af; font-size: 12px; margin: 5px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Charge Pay</h1>
                        <p>Réclamation</p>
                    </div>
                    
                    <h2>Bonjour %s,</h2>
                    <p>Le statut de votre réclamation <strong>"%s"</strong> a été mis à jour :</p>
                    
                    <div style="text-align: center; margin: 25px 0;">
                        <span class="status-badge">%s</span>
                    </div>
                    
                    <div class="response-box">
                        <p><strong>Réponse de l'administrateur :</strong></p>
                        <p>%s</p>
                    </div>
                    
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement. Merci de ne pas y répondre.</p>
                        <p>© 2024 Charge Pay. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(statusColor, nom, subject, statusLabel, response);
    }
}
