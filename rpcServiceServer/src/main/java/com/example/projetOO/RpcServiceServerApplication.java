package com.example.projetOO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Import;
import com.example.projetOO.service.ListService;
import com.example.projetOO.service.UserService;
import com.example.projetOO.service.WorkService;

@SpringBootApplication(scanBasePackages = "com.example.projetOO.rpc")
@EntityScan("com.example.projetOO.entities")
@EnableJpaRepositories("com.example.projetOO.repository")
@Import({WorkService.class, UserService.class, ListService.class})
public class RpcServiceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpcServiceServerApplication.class, args);
    }
}
