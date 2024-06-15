package org.vgcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.vgcs")
public class IntegrationServiceApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(IntegrationServiceApplication.class, args)));
    }
}