package ru.idfedorov09.kotbot.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    fun sendEmail(toEmail: String, subject: String, body: String) {
        val message = SimpleMailMessage().apply {
            // TODO
            from = "mailtrap@sno-team.ru"
            setTo(toEmail)
            setSubject(subject)
            setText(body)
        }
        mailSender.send(message)
    }
}