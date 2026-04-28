package com.studyromm.gateway.api;

import com.studyromm.platform.service.task.TaskStatus;
import com.studyromm.platform.service.task.TaskStatusResponse;
import com.studyromm.gateway.trace.TraceIdWebFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayTaskController {

    @GetMapping("/api/common/tasks/{taskId}")
    public TaskStatusResponse getTask(
            @PathVariable String taskId,
            @RequestHeader(name = TraceIdWebFilter.TRACE_ID_HEADER, required = false) String traceId
    ) {
        return new TaskStatusResponse(
                taskId,
                "BOOTSTRAP",
                TaskStatus.PENDING,
                0,
                "gateway bootstrap task contract ready",
                null,
                null,
                true,
                traceId
        );
    }
}
