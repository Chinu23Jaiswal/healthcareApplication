package com.healthcareApp.healthcareApp.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.healthcareApp.healthcareApp.entity.DiagnosisMail;
import com.healthcareApp.healthcareApp.entity.MailingDao;
import com.healthcareApp.healthcareApp.entity.Prescription;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.logging.Logger;

@Slf4j
@Service
public class KafkaListeners {
    @Autowired
    private JavaMailSender javaMailSender;



    @KafkaListener(
            topics="topic",
            groupId="groupId",
            containerFactory = "kafkaListenerContainerFactory"
    )
    void listener(@Payload MailingDao mailingDao, @Headers MessageHeaders messageHeaders) throws MessagingException, JsonProcessingException, javax.mail.MessagingException {
        log.info("Payload received : {}"
                +mailingDao);
        sendEmailToDoctor(mailingDao);
    }

    @KafkaListener(topics = "topic", groupId = "groupId1", containerFactory = "diagnosisMailConcurrentKafkaListenerContainerFactory")
    void diagnosisListener(@Payload DiagnosisMail diagnosisMail, @Headers MessageHeaders messageHeaders) throws MessagingException, JsonProcessingException {
        sendEmailToPatient(diagnosisMail);
    }
    public void sendEmailToDoctor(MailingDao mailingDao) throws javax.mail.MessagingException {

        String from = "solutionshealthcare3@gmail.com";
        String to = "cj32aiswal@gmail.com";

        log.info("to email : {}"+ to);

//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//        helper.setSubject("MedApp | Hi Dr." + mailingDao.getDoctorName() + "! You have a new appointment from " + mailingDao.getPatientName());
//        helper.setFrom(from);
//        helper.setTo(to);

        String text = "Appointment\n" +
                "Please find the below illness details" +"\n\n"+
                "Appointment ID:" + mailingDao.getAppointmentId()+"\n\n"+
                "Name: " + mailingDao.getPatientName() + "\n\n" +
                "Illness: " + mailingDao.getPatientIllness() + "\n\n" +
                "Contact: " + mailingDao.getPatientContact() + "\n\n" +
                "Email: " + mailingDao.getDoctorEmail() + "\n\n" +
                "Age: " + mailingDao.getPatientAge() + "\n\n";

        sendEmail(from,text,to);
        //javaMailSender.send(message);
    }
    public void sendEmail(String toEmail, String body, String subject){
        log.info("Sending Email now");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("solutionshealthcare3@gmail.com");
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setText(body);
        simpleMailMessage.setSubject(subject);

        javaMailSender.send(simpleMailMessage);
        log.info("Email has been sent!");
    }

    public void sendEmailToPatient(DiagnosisMail diagnosisMail) throws MessagingException {
        String from = "solutionshealthcare3@gmail.com";
        String to = "cj32aiswal@gmail.com";//diagnosisMail.getPatientEmail();

        String text = "Appointment\n" +
                "Please find the below illness details" +"\n\n"+
                "Appointment ID: " + diagnosisMail.getAppointmentId() + "\n\n" +
                "Name: " + diagnosisMail.getPatientName() + "\n\n" +
                "Illness: " + diagnosisMail.getPrescription() + "\n\n" +
                "Contact: " + diagnosisMail.getPatientContact() + "\n\n" +
                "Email: " + diagnosisMail.getDoctorEmail() + "\n\n" +
                "Age: " + diagnosisMail.getPatientEmail() + "\n\n";
        sendEmail(from,text,to);
        log.info("Email sent to the doctor");
    }


}
