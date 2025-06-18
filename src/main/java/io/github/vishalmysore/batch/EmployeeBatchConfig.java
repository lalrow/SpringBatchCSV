package io.lalitrow.batch;

import io.lalitrow.batch.model.Employee;
import io.lalitrow.batch.repository.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EmployeeBatchConfig {

    @Bean
    public MultiResourceItemReader<Employee> multiResourceItemReader(
            @Value("classpath:input/*.csv") Resource[] resources) {
        return new MultiResourceItemReaderBuilder<Employee>()
                .name("multiResourceItemReader")
                .resources(resources)
                .delegate(employeeItemReader())
                .build();
    }

    @Bean
    public FlatFileItemReader<Employee> employeeItemReader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReaderBuilder<Employee>()
                .name("employeeItemReader")
                .linesToSkip(1)
                .delimited()
                .names("id", "name", "salary")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Employee.class);
                }})
                .strict(false)
                .build();
        reader.setLineMapper((line, lineNumber) -> {
            if (line.trim().isEmpty()) {
                return null; // skip blank lines silently
            }
            org.springframework.batch.item.file.transform.DelimitedLineTokenizer tokenizer = new org.springframework.batch.item.file.transform.DelimitedLineTokenizer();
            tokenizer.setNames("id", "name", "salary");
            org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper<>();
            fieldSetMapper.setTargetType(Employee.class);
            return fieldSetMapper.mapFieldSet(tokenizer.tokenize(line));
        });
        return reader;
    }

    @Bean
    public RepositoryItemWriter<Employee> writer(EmployeeRepository repository) {
        RepositoryItemWriter<Employee> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, MultiResourceItemReader<Employee> reader, RepositoryItemWriter<Employee> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Employee, Employee>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importEmployeeJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importEmployeeJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }
}
