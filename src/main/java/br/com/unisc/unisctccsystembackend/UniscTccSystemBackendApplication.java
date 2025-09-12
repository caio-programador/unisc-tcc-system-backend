package br.com.unisc.unisctccsystembackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "br.com.unisc.unisctccsystembackend")         
@EnableJpaRepositories(basePackages = "br.com.unisc.unisctccsystembackend")
public class UniscTccSystemBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniscTccSystemBackendApplication.class, args);
    }
}
