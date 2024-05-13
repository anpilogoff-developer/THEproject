package ru.anpilogoff_dev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {
    private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private  Properties mailProperties;

    public EmailService() {
        try (InputStream input = EmailService.class.getClassLoader().getResourceAsStream("mail.properties")) {
            this.mailProperties = new Properties();

            if (input == null) {
                log.debug("Не удалось найти файл mail.properties");
                return;
            }
            // Загрузка файла properties
            mailProperties.load(input);
        } catch (Exception e) {
            log.debug(new StringBuilder("ошибка при загрузки файла mail.property").append(e.getMessage()));
        }
    }

    public void sendEmail(String email, String confirmCode) {
        // Создание сессии с настройками из файла properties
        Session session = Session.getInstance(mailProperties);

        // Создание сообщения
        javax.mail.Message message =  new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.smtp.sender")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Signup email confirmation");
            StringBuilder messageText = new StringBuilder(
                    "Подтвердите регистрацию:\nhttps://anpilogoff-dev.ru/signup?confirmation=").append(confirmCode);
            message.setText(messageText.toString());
            // Отправка сообщения
            Transport.send(message);
        } catch (MessagingException e) {
            log.debug(new StringBuilder("Error occured during sendEmail() method execution:\n").append(e.getMessage()));
            throw new RuntimeException(e);
        }
    }
}



