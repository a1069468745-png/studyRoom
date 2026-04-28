package com.studyromm.platform.web.task;

import com.studyromm.platform.service.task.TaskStatus;
import com.studyromm.platform.service.task.TaskStatusResponse;
import com.studyromm.platform.web.trace.TraceIdHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonTaskController {

    @GetMapping("/api/common/tasks/{taskId}")
    public TaskStatusResponse getTask(@PathVariable String taskId) {
        return new TaskStatusResponse(
                taskId,
                "BOOTSTRAP",
                TaskStatus.PENDING,
                0,
                "bootstrap task contract ready",
                null,
                null,
                true,
                TraceIdHolder.getTraceId()
        );
    }
}
