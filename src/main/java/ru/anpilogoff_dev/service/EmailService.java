package ru.anpilogoff_dev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Сервис для отправки электронных писем
 */
public class EmailService {
    private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private final Properties mailProperties;

    /**
     * Конструктор внутри которого происходит создание потока ввода для чтения файла "mail.properties", находящегося
     * в classpath приложения
     */
    public EmailService() {
        try(InputStream input = EmailService.class.getClassLoader().getResourceAsStream("mail.properties")){
            this.mailProperties = new Properties();
            if (input == null) {
                log.debug("Не удалось найти файл mail.properties");
            } else {
                this.mailProperties.load(input);
            }
        } catch (IOException e) {
            log.debug((new StringBuilder("ошибка при загрузки файла mail.property")).append(e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    /**
     * Отправляет  e-mail, содержащий url для подтверждения регистрации
     *
     * @param email адрес электронной почты зарегистрировавшегося пользователя.
     * @param confirmCode Код подтверждения, сгенерированный в процессе регистрации.
     */
    public void sendConfirmationEmail(String email, String confirmCode) {
        Session session = Session.getInstance(mailProperties);
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.smtp.sender")));
            message.setRecipients(RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Signup email confirmation");
            StringBuilder messageText = (new StringBuilder("Подтвердите регистрацию:\nhttps://anpilogoff-dev.ru/signup?confirmation=")).append(confirmCode);
            message.setText(messageText.toString());
            Transport.send(message);
        } catch (MessagingException e) {
            log.debug((new StringBuilder("Error occured during sendEmail() method execution:\n")).append(e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    public void sendConfirmationSuccessNotification(String email){}
}
