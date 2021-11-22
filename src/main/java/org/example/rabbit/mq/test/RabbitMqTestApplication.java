package org.example.rabbit.mq.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"org.example.rabbit.mq.test", "com.azericard.core.config"})
//@EnableScheduling
@EnableSwagger2
public class RabbitMqTestApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqTestApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RabbitMqTestApplication.class);
    }
}
