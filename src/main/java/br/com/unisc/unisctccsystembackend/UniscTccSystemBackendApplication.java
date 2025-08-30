package br.com.unisc.unisctccsystembackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "br.com.unisc.unisctccsystembackend.entities")
public class UniscTccSystemBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(UniscTccSystemBackendApplication.class, args);
  }

}
