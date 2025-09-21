package com.etl.policy.config;

import com.etl.policy.batch.processor.PdfProcessor;
import com.etl.policy.batch.reader.PdfReader;
import com.etl.policy.batch.writer.PdfWriter;
import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.parser.TrafficKaskoParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.UncheckedIOException;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  @Bean
  public Job pdfIngestJob(JobRepository repo, Step parseStep) {
    return new JobBuilder("pdfIngestJob", repo)
        .start(parseStep)
        .build();
  }

  @Bean
  public Step parseStep(JobRepository repo,
                        PlatformTransactionManager tx,
                        PdfReader reader,
                        PdfProcessor processor,
                        PdfWriter writer) {
    return new StepBuilder("parseStep", repo)
        .<PdfStore, TrafficKaskoParser.ParsedOffer>chunk(10, tx)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .retry(UncheckedIOException.class)
        .retryLimit(3)
        .skip(Exception.class)
        .skipLimit(100)
        .build();
  }
}
