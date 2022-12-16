package openproject.where42;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableRetry
@SpringBootApplication
public class Where42Application {

    public static void main(String[] args) {
        SpringApplication.run(Where42Application.class, args);
    }

}
