package com.therapy.nest.shared.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${spring.rabbitmq.template.default-receive-queue}")
    private String amqpQueue;

    @Value("${spring.rabbitmq.template.exchange}")
    private String amqpExchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String amqpRoutingKey;

    @Value("${spring.rabbitmq.host}")
    private String amqpHost;

    @Value("${spring.rabbitmq.port}")
    private String amqpPort;

    @Value("${spring.rabbitmq.username}")
    private String amqpUsername;

    @Value("${spring.rabbitmq.password}")
    private String amqpPassword;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        logger.info("[CachingConnectionFactory] ✅: RabbitMQ connection established successfully!");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpHost, Integer.parseInt(amqpPort));
        connectionFactory.setUsername(amqpUsername);
        connectionFactory.setPassword(amqpPassword);
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(amqpQueue).build();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(amqpExchange);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(amqpRoutingKey);
    }
}
