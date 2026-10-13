package es.safareyes.cafeteria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"es.safareyes.cafeteria", "repositorios", "modelos", "dtos"})
@EntityScan(basePackages = {"modelos", "es.safareyes.cafeteria"})
@EnableJpaRepositories(basePackages = {"repositorios", "es.safareyes.cafeteria"})
public class CafeteriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeteriaApplication.class, args);
    }

}
