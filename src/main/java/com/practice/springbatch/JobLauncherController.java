package com.practice.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class JobLauncherController {

    private final Job job;
    private final JobLauncher jobLauncher;

    public JobLauncherController(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    @PostMapping("/batch-sync")
    public String launchSync(@RequestBody Member member) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("id", member.getId())
                .addDate("date", new Date())
                .toJobParameters();

        jobLauncher.run(job, jobParameters);

        return "batch executed";
    }


    @PostMapping("/batch-async")
    public String launchAsync(@RequestBody Member member) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("id", member.getId())
                .addDate("date", new Date())
                .toJobParameters();

        TaskExecutorJobLauncher taskExecutorJobLauncher = (TaskExecutorJobLauncher) jobLauncher; // 스프링 배치 5버전부터 TaskExecutorJobLauncher 로 통일
        taskExecutorJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // 비동기 설정

        taskExecutorJobLauncher.run(job, jobParameters);

        return "batch executed";
    }

}
