package com.practice.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ExecutionContextConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ExecutionContextTasklet1 executionContextTasklet1;
    private final ExecutionContextTasklet2 executionContextTasklet2;
    private final ExecutionContextTasklet3 executionContextTasklet3;
    private final ExecutionContextTasklet4 executionContextTasklet4;

    public ExecutionContextConfiguration(JobRepository jobRepository,
                                         PlatformTransactionManager transactionManager,
                                         ExecutionContextTasklet1 executionContextTasklet1,
                                         ExecutionContextTasklet2 executionContextTasklet2,
                                         ExecutionContextTasklet3 executionContextTasklet3,
                                         ExecutionContextTasklet4 executionContextTasklet4) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.executionContextTasklet1 = executionContextTasklet1;
        this.executionContextTasklet2 = executionContextTasklet2;
        this.executionContextTasklet3 = executionContextTasklet3;
        this.executionContextTasklet4 = executionContextTasklet4;
    }

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .start(step1())
                .next(step2())
                .next(step3())
                .next(step4())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(executionContextTasklet1, transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(executionContextTasklet2, transactionManager)
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet(executionContextTasklet3, transactionManager)
                .build();
    }

    @Bean
    public Step step4() {
        return new StepBuilder("step4", jobRepository)
                .tasklet(executionContextTasklet4, transactionManager)
                .build();
    }
}
