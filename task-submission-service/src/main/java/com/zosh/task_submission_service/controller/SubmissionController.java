package com.zosh.task_submission_service.controller;

import com.zosh.task_submission_service.dtos.UserDto;
import com.zosh.task_submission_service.model.Submission;
import com.zosh.task_submission_service.service.SubmissionService;
import com.zosh.task_submission_service.service.TaskService;
import com.zosh.task_submission_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @PostMapping()
    public ResponseEntity<Submission> submitTask(
            @RequestParam Long task_id,
            @RequestParam String github_link,
            @RequestHeader("Authorization") String jwt) throws Exception {

        UserDto user=userService.getUserProfileHandler(jwt);
        Submission submission = submissionService.submitTask(task_id, github_link, user.getId(), jwt);
        System.out.println("submission - "+submission);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);

    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<Submission> getTaskSubmissionById(@PathVariable Long submissionId) {
        try {
            Submission submission = submissionService.getTaskSubmissionById(submissionId);
            return new ResponseEntity<>(submission, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<List<Submission>> getAllTaskSubmissions() {
        List<Submission> submissions = submissionService.getAllTaskSubmissions();
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Submission>> getTaskSubmissionsByTaskId(@PathVariable Long taskId) {
        List<Submission> submissions = submissionService.getTaskSubmissionsByTaskId(taskId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Submission> acceptOrDeclineTaskSubmission(
            @PathVariable Long id,
            @RequestParam("status")String status) throws Exception {
        Submission submission = submissionService.acceptDeclineSubmission(id,status);

        if(submission.getStatus().equals("COMPLETE")){
            taskService.completeTask(submission.getTaskId());
        }

        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

}
