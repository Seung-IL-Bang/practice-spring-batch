package com.practice.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JdbcCursorConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public JdbcCursorConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("step", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(customItemReader())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Customer> customItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .fetchSize(10) // chunk size와 동일하게 설정
                .sql("SELECT id, firstName, lastName, birthdate" +
                        " FROM customer" +
                        " WHERE firstName" +
                        " LIKE ? ORDER BY lastName, firstName")
                .beanRowMapper(Customer.class) // setter 를 이용한 매핑
                .queryArguments("A%")
                .maxItemCount(100)
                .currentItemCount(0)
                .maxRows(100)
                .build();
    }

    @Bean
    public ItemWriter<Customer> customItemWriter() {
        return chunk -> {
            System.out.println(">> current chunk size: " + chunk.size());
            Thread.sleep(1000);
            for (Customer customer : chunk.getItems()) {
                System.out.println(customer);
            }
        };
    }

}
