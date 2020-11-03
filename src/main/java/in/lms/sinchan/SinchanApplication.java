package in.lms.sinchan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SinchanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SinchanApplication.class, args);
    }

}
