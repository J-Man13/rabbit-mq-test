package org.example.rabbit.mq.test.configuration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.amqp.rabbit.connection.PooledChannelConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Configuration
public class RabbitMqClientConf {

    @Bean
    public PooledChannelConnectionFactory clientProducerConnectionFactory(final @Value("${spring.rabbitmq.host}") String host,
                                                                          final @Value("${spring.rabbitmq.port}") int port,
                                                                          final @Value("${spring.rabbitmq.username}") String username,
                                                                          final @Value("${spring.rabbitmq.password}") String password) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        for (int i = 0; i < 2 ; i++)
            connectionFactory.newConnection();

        PooledChannelConnectionFactory pcf = new PooledChannelConnectionFactory(connectionFactory);
        pcf.setPoolConfigurer((pool, tx) -> {
            for (int i = 0; i < 3 ; i++)
                poolAddObjectRuntime(pool);
        });

        return pcf;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final @Qualifier("clientProducerConnectionFactory") PooledChannelConnectionFactory pooledChannelConnectionFactory,
                                         final MessageConverter commonMessageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(pooledChannelConnectionFactory);
        rabbitTemplate.setMessageConverter(commonMessageConverter);
        return rabbitTemplate;
    }

    private void poolAddObjectRuntime(final GenericObjectPool<Channel> channelGenericObjectPool){
        try {
            channelGenericObjectPool.addObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
