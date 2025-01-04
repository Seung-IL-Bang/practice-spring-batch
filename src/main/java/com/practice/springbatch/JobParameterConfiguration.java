package com.practice.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;

@Configuration
public class JobParameterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public JobParameterConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job() {
        return new JobBuilder("job", jobRepository)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("========== step1 ==========");
                        // StepContribution 으로 JobParameters 조회하기.
                        JobParameters jobParameters = contribution
                                .getStepExecution()
                                .getJobExecution()
                                .getJobParameters();

                        String name = jobParameters.getString("name");
                        Long age = jobParameters.getLong("age");
                        Date date = jobParameters.getDate("date");
                        Double amount = jobParameters.getDouble("amount");

                        System.out.println("name = " + name);
                        System.out.println("age = " + age);
                        System.out.println("date = " + date);
                        System.out.println("amount = " + amount);

                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("========== step2 ==========");
                        // ChunkContext 으로 JobParameters 조회하기.
                        JobParameters jobParameters = chunkContext
                                .getStepContext()
                                .getStepExecution()
                                .getJobExecution()
                                .getJobParameters();

                        String name = jobParameters.getString("name");
                        Long age = jobParameters.getLong("age");
                        Date date = jobParameters.getDate("date");
                        Double amount = jobParameters.getDouble("amount");

                        System.out.println("name = " + name);
                        System.out.println("age = " + age);
                        System.out.println("date = " + date);
                        System.out.println("amount = " + amount);

                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }
}
