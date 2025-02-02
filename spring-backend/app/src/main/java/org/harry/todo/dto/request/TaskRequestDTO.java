package org.harry.todo.dto.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.harry.todo.entities.Priority;

import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskRequestDTO {

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime targetDate;
    private Priority priority;
    private Integer categoryId;
    private Boolean completed;

}
