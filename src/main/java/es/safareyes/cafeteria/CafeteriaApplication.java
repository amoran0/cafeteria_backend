package es.safareyes.cafeteria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(scanBasePackages = {
        "es.safareyes.cafeteria", "controller", "service", "repositorios", "modelos", "dtos",
        "exceptions", "config", "integracion"
})
@EntityScan(basePackages = {"modelos", "es.safareyes.cafeteria"})
@EnableJpaRepositories(basePackages = {"repositorios", "es.safareyes.cafeteria"})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class CafeteriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeteriaApplication.class, args);
    }

}
