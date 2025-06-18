package io.lalitrow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchCsvApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchCsvApplication.class, args);
    }
}
