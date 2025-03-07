package fpt.aptech.server_be.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String REQUEST_QUEUE = "rabbit_mq_queue";
    public static final String REPLY_QUEUE = "rabbit_mq_reply_queue";
    public static final String DLQ = "rabbit_mq_dlq";
    public static final String EXCHANGE = "rabbit_mq_exchange";
    public static final String ROUTING_KEY = "rabbit_mq_r_key";
    public static final String DLQ_ROUTING_KEY = "dlq_r_key";


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUri("amqps://ixvxgtrj:NuqKkp-eIyM5bLWNNxeECbNiU-7W_V8_@fuji.lmq.cloudamqp.com/ixvxgtrj");
        return factory;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange("dead_letter_exchange");
    }

    @Bean
    public Queue requestQueue() {
        return QueueBuilder.durable(REQUEST_QUEUE)
                .lazy() // Giảm tải RAM
                .withArgument("x-dead-letter-exchange", "dead_letter_exchange")
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue replyQueue() {
        return new Queue(REPLY_QUEUE, true);
    }

    @Bean
    public Queue dlq() {
        return new Queue(DLQ);
    }

    @Bean
    public Binding binding(Queue requestQueue, DirectExchange exchange) {
        return BindingBuilder.bind(requestQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, DirectExchange dlqExchange) {
        return BindingBuilder.bind(dlq).to(dlqExchange).with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate getTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.out.println("Message failed: " + cause);
            }
        });
        return rabbitTemplate;
    }

}
