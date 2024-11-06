package ru.idfedorov09.kotbot.service

import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val emailService: EmailService,
) {

    // TODO: вынести тему и сообщение в ресурсы
    /**
     * Отправляет письмо с кодом подтверждения. Возвращает код подтверждения
     */
    fun sendVerificationEmail(toEmail: String): String? {
        val code = generateVerifyCode()
        val message = "Код подтверждения: $code"
        val subject = "Подтверждение входа в бота Telegram"
        runCatching {
            emailService.sendEmail(
                toEmail,
                subject,
                message
            )
        }.onFailure {
            return null
        }
        return code
    }

    private fun generateVerifyCode(): String {
        val code = Random.nextInt(100000, 999999).toString()
        return code
    }
}