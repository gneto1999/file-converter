package dev.gneto.file_converter.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String QUEUE = "converter.queue";
    public static final String EXCHANGE = "converter.exchange";
    public static final String ROUTING_KEY = "converter.key";

    public static final String DLQ = "converter.queue.dlq";
    public static final String DLQ_EXCHANGE = "converter.exchange.dlx";
    public static final String DLQ_ROUTING_KEY = "converter.dlq";

    @Bean
    public DirectExchange converterExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean DirectExchange dlqExchange() {
        return new DirectExchange(DLQ);
    }

    @Bean
    public Queue converterQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DLQ_EXCHANGE)
                .deadLetterRoutingKey(DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding converterBinding() {
        return BindingBuilder.bind(converterQueue())
                .to(converterExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue())
                .to(dlqExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
