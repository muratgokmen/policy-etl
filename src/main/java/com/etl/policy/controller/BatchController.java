package com.etl.policy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job pdfIngestJob;
    private final JobExplorer jobExplorer;

    @PostMapping("/start-pdf-processing")
    public ResponseEntity<Map<String, Object>> startPdfProcessing() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Her çalıştırma için unique parametre (aynı job'ı tekrar çalıştırabilmek için)
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("startTime", LocalDateTime.now().toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Starting PDF processing batch job with parameters: {}", jobParameters);
            
            JobExecution jobExecution = jobLauncher.run(pdfIngestJob, jobParameters);
            
            response.put("success", true);
            response.put("jobExecutionId", jobExecution.getId());
            response.put("jobInstanceId", jobExecution.getJobInstance().getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("startTime", jobExecution.getStartTime());
            response.put("message", "PDF processing batch job started successfully");
            
            log.info("PDF processing batch job started. Execution ID: {}, Status: {}", 
                    jobExecution.getId(), jobExecution.getStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("Job is already running", e);
            response.put("success", false);
            response.put("error", "Job is already running");
            return ResponseEntity.badRequest().body(response);
            
        } catch (JobRestartException e) {
            log.error("Job restart failed", e);
            response.put("success", false);
            response.put("error", "Job restart failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (JobInstanceAlreadyCompleteException e) {
            log.warn("Job instance already completed", e);
            response.put("success", false);
            response.put("error", "Job instance already completed");
            return ResponseEntity.badRequest().body(response);
            
        } catch (JobParametersInvalidException e) {
            log.error("Invalid job parameters", e);
            response.put("success", false);
            response.put("error", "Invalid job parameters: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Unexpected error starting batch job", e);
            response.put("success", false);
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status/{jobExecutionId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable Long jobExecutionId) {
        try {
            // JobExplorer ile execution'ı al
            JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
            
            if (jobExecution == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Job execution not found with ID: " + jobExecutionId);
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> status = new HashMap<>();
            status.put("jobExecutionId", jobExecution.getId());
            status.put("jobName", jobExecution.getJobInstance().getJobName());
            status.put("status", jobExecution.getStatus().toString());
            status.put("exitStatus", jobExecution.getExitStatus().getExitCode());
            status.put("startTime", jobExecution.getStartTime());
            status.put("endTime", jobExecution.getEndTime());
            
            // Duration hesaplama düzeltmesi (LocalDateTime için)
            Long duration = null;
            if (jobExecution.getEndTime() != null && jobExecution.getStartTime() != null) {
                LocalDateTime startTime = jobExecution.getStartTime();
                LocalDateTime endTime = jobExecution.getEndTime();
                // LocalDateTime'ı epoch milisaniyeye çevir
                long startMillis = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                long endMillis = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                duration = endMillis - startMillis;
            }
            status.put("duration", duration);
            
            // Step bilgileri
            Map<String, Object> stepInfo = new HashMap<>();
            for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                Map<String, Object> stepDetails = new HashMap<>();
                stepDetails.put("status", stepExecution.getStatus().toString());
                stepDetails.put("readCount", stepExecution.getReadCount());
                stepDetails.put("writeCount", stepExecution.getWriteCount());
                stepDetails.put("commitCount", stepExecution.getCommitCount());
                stepDetails.put("rollbackCount", stepExecution.getRollbackCount());
                stepDetails.put("skipCount", stepExecution.getSkipCount());
                stepDetails.put("filterCount", stepExecution.getFilterCount());
                stepInfo.put(stepExecution.getStepName(), stepDetails);
            }
            status.put("steps", stepInfo);
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("Error retrieving job status for execution ID: {}", jobExecutionId, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error retrieving job status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<String>> getAllJobs() {
        try {
            // Bu örnekte sadece pdfIngestJob var
            List<String> jobs = List.of(pdfIngestJob.getName());
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            log.error("Error retrieving job list", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/job-executions")
    public ResponseEntity<Map<String, Object>> getRecentJobExecutions(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Son job instance'ları al
            List<JobInstance> jobInstances = jobExplorer.getJobInstances(pdfIngestJob.getName(), 0, limit);
            
            List<Map<String, Object>> executionSummaries = new java.util.ArrayList<>();
            
            for (JobInstance instance : jobInstances) {
                List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
                
                for (JobExecution execution : executions) {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("jobExecutionId", execution.getId());
                    summary.put("jobInstanceId", instance.getId());
                    summary.put("status", execution.getStatus().toString());
                    summary.put("startTime", execution.getStartTime());
                    summary.put("endTime", execution.getEndTime());
                    
                    executionSummaries.add(summary);
                }
            }
            
            response.put("executions", executionSummaries);
            response.put("jobName", pdfIngestJob.getName());
            response.put("totalFound", executionSummaries.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving job executions", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error retrieving job executions: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
