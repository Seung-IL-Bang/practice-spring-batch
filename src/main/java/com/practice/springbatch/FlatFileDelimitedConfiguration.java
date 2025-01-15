package com.practice.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlatFileDelimitedConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public FlatFileDelimitedConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(itemReader2())
                .writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        for (Customer customer : chunk.getItems()) {
                            System.out.println("Customer: " + customer);
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Step 2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(new ClassPathResource("customers.csv"))
                .fieldSetMapper(new CustomerFieldSetMapper())
                .delimited().delimiter(",")
                .names(new String[]{"name", "age", "year"})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader2() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(new ClassPathResource("customers.csv"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .delimited().delimiter(",")
                .names(new String[]{"name", "age", "year"})
                .linesToSkip(1)
                .build();
    }
}
