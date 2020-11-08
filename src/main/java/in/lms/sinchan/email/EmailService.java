package in.lms.sinchan.email;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailService {

    @Autowired
    private GmailProvider gmailProvider;

    @Value("${reset.password.email.host}")
    private String host;
    @Value("${reset.password.email.port}")
    private int port;
    @Value("${reset.password.email.id}")
    private String emailId;
    @Value("${reset.password.email.pwd}")
    private String password;
    @Value("${reset.password.email.provider}")
    private String provider;

    public String sendMail(ModelMap modelMap) {
        log.info(":::::Inside EmailService Class, sendEmail method:::::");
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        log.info(":::::host {}, port {}, emailID {}, password {}, provider {}", host, port, emailId,
                        password, provider);
        javaMailSenderImpl.setHost(host);
        javaMailSenderImpl.setPort(port);
        javaMailSenderImpl.setUsername(emailId);
        javaMailSenderImpl.setPassword(password);
        javaMailSenderImpl.setJavaMailProperties(getJavaMailProperties(provider));
        MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setTo((String) modelMap.get("to"));
            helper.setSubject((String) modelMap.get("subject"));
            helper.setText((String) modelMap.get("body"));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        javaMailSenderImpl.send(mimeMessage);
        return "Mail sent successfully";
    }

    private Properties getJavaMailProperties(String provider) {
        log.info(":::::Setting javaMailProperties :::::");
        Properties properties = new Properties();
        if (provider.equalsIgnoreCase("GMAIL")) {
            getJavaGmailProperties(properties);
        }
        log.info("::::properties {}", properties);
        return properties;
    }

    private void getJavaGmailProperties(Properties properties) {
        log.info(":::::Filling the properties::::");
        gmailProvider.getGmailProperties().entrySet().stream()
                        .forEach(m -> properties.put(m.getKey(), m.getValue()));
    }

}
