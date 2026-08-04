package app.mendnook.materials;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MendNookMaterialsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MendNookMaterialsApplication.class, args);
    }
}
