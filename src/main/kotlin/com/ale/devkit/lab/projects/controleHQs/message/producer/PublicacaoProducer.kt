package com.ale.devkit.lab.projects.controleHQs.message.producer

import com.ale.devkit.lab.projects.controleHQs.dto.ColecaoMessage
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class PublicacaoProducer(
    private val rabbitTemplate: RabbitTemplate
) {

    private val log = LoggerFactory.getLogger(PublicacaoProducer::class.java)

    fun enviarMsgParaFila(message: ColecaoMessage) {

        log.info("Enviando mensagem para fila: id='{}', titulo='{}'",
            message.id,
            message.titulo
        )

        try {
            rabbitTemplate.convertAndSend("colecao.enriquecimento", message)
            log.info("Mensagem enviada com sucesso: id='{}'", message.id)
        } catch (ex: Exception) {
            log.error(
                "Erro ao enviar mensagem para fila: id='{}', erro='{}'",
                message.id,
                ex.message,
                ex
            )

            throw RuntimeException("Erro ao enviar mensagem para fila")
        }
    }
}