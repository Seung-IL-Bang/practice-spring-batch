package com.practice.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ItemReaderAdapterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ItemReaderAdapterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
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
                .<String, String>chunk(10, transactionManager)
                .reader(customItemReader())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public ItemReaderAdapter<String> customItemReader() {
        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(customerService());
        reader.setTargetMethod("joinCustomer");
        return reader;
    }

    private CustomerService<String> customerService() {
        return new CustomerService<>();
    }

    @Bean
    public ItemWriter<String> customItemWriter() {
        return chunk -> System.out.println(chunk.getItems());
    }
}
