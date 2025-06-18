package io.lalitrow.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchCsvApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchCsvApplication.class, args);
    }
}
