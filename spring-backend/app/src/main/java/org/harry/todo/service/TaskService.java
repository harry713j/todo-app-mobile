package org.harry.todo.service;

import jakarta.servlet.http.HttpServletRequest;
import org.harry.todo.dto.request.TaskRequestDTO;
import org.harry.todo.entities.Category;
import org.harry.todo.entities.Priority;
import org.harry.todo.entities.Task;
import org.harry.todo.entities.User;
import org.harry.todo.repository.CategoryRepository;
import org.harry.todo.repository.TaskRepository;
import org.harry.todo.repository.UserRepository;
import org.harry.todo.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;



    public Task createTask(TaskRequestDTO taskRequestDTO){

        ValidationUtil.validateTask(taskRequestDTO);

        String accessToken = resolveToken(request);

        if (accessToken == null){
            throw new RuntimeException("Invalid JWT token");
        }

        String username = jwtService.extractUsername(accessToken);

        Task task = Task.builder()
                .title(taskRequestDTO.getTitle())
                .description(taskRequestDTO.getDescription())
                .createdAt(taskRequestDTO.getCreatedAt())
                .targetDate(taskRequestDTO.getTargetDate())
                .priority(taskRequestDTO.getPriority())
                .completed(taskRequestDTO.getCompleted())
                .category(getCategory(taskRequestDTO.getCategoryId()))
                .user(getUser(username))
                .build();

        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, TaskRequestDTO taskRequestDTO){
        Task existingTask = getTask(taskId);

        if (taskRequestDTO.getCreatedAt().isAfter(taskRequestDTO.getTargetDate())){
            throw new RuntimeException("Invalid date and time information");
        }

        if (taskRequestDTO.getTitle() != null){
            existingTask.setTitle(taskRequestDTO.getTitle());
        }

        if (taskRequestDTO.getDescription() != null){
            existingTask.setDescription(taskRequestDTO.getDescription());
        }

        if (taskRequestDTO.getPriority() != null){
            existingTask.setPriority(taskRequestDTO.getPriority());
        }

        if (taskRequestDTO.getTargetDate() != null){
            existingTask.setTargetDate(taskRequestDTO.getTargetDate());
        }

        if (!Objects.equals(taskRequestDTO.getCategoryId(),
                existingTask.getCategory().getCategoryId())){
            existingTask.setCategory(getCategory(taskRequestDTO.getCategoryId()));
        }

        existingTask.setCompleted(taskRequestDTO.getCompleted());

        return taskRepository.save(existingTask);

    }

    public Task getTask(Long taskId){
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with task id: " + taskId));
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public List<Task> getAllCompletedTasks(){
        return getAllTasks().stream()
                .filter(Task::getCompleted)
                .toList();
    }

    public List<Task> getAllInCompletedTasks(){
        return getAllTasks().stream()
                .filter(task -> !task.getCompleted())
                .toList();
    }

    public void deleteTask(Long taskId){
        Task task = getTask(taskId);
        taskRepository.delete(task);
    }

    public void deleteAllCompletedTasks(List<Long> taskIds){
        List<Task> tasks = taskRepository.findAllById(taskIds);

        taskRepository.deleteAll(tasks);
    }

    public List<Task> searchTasks(String title, Priority priority, Boolean completed, Long categoryId){

        return taskRepository.searchTasksByCriteria(title,priority, completed, categoryId);
    }

    public List<Task> getAllSortedTasks(Sort sort){
        return taskRepository.findAll(sort);
    }


    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        return null;
    }

    private Category getCategory(Integer categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private User getUser(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username, User not found"));
    }
}
