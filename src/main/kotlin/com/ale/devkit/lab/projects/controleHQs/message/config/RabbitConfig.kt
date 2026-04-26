package com.ale.devkit.lab.projects.controleHQs.message.config

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    companion object {
        const val QUEUE_ENRIQUECIMENTO = "colecao.enriquecimento"
        const val DLQ_ENRIQUECIMENTO = "colecao.enriquecimento.dlq"
        const val DLQ_EXCHANGE = "colecao.enriquecimento.dlq.exchange"
    }

    @Bean
    fun filaEnriquecimento() = QueueBuilder.durable(QUEUE_ENRIQUECIMENTO)
        .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
        .build()

    @Bean
    fun dlqEnriquecimento() = QueueBuilder.durable(DLQ_ENRIQUECIMENTO).build()

    @Bean
    fun dlqExchange() = DirectExchange(DLQ_EXCHANGE)

    @Bean
    fun dlqBinding() = BindingBuilder
        .bind(dlqEnriquecimento())
        .to(dlqExchange())
        .with(DLQ_ENRIQUECIMENTO)

    @Bean
    fun messageConverter(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            messageConverter = messageConverter()
        }
    }

}