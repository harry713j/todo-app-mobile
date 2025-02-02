package org.harry.todo.controller;

import org.harry.todo.dto.request.TaskRequestDTO;
import org.harry.todo.entities.Priority;
import org.harry.todo.entities.Task;
import org.harry.todo.service.CategoryService;
import org.harry.todo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TaskService taskService;

    @PostMapping("")
    public ResponseEntity<?> registerTask(@RequestBody TaskRequestDTO taskRequestDTO){
        try{
            Task task = taskService.createTask(taskRequestDTO);

            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDTO taskRequestDTO){
        try{
            Task updatedTask = taskService.updateTask(taskId, taskRequestDTO);

            return new ResponseEntity<>(updatedTask, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update the task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable Long taskId){
        try{
            return new ResponseEntity<>(taskService.getTask(taskId), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get the task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(){
        try {
            return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get all the task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/complete")
    public ResponseEntity<?> getAllCompletedOrNot(@RequestParam(required = true) Boolean completed){
        try{
            return completed ?
                    new ResponseEntity<>(taskService.getAllCompletedTasks(), HttpStatus.OK)
                    : new ResponseEntity<>(taskService.getAllInCompletedTasks(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get tasks",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId){
        try {
            taskService.deleteTask(taskId);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-completed-tasks")
    public ResponseEntity<String> deleteAllCompletedTasks(@RequestParam List<Long> taskIds){
        try{
            taskService.deleteAllCompletedTasks(taskIds);
            return new ResponseEntity<>("All completed tasks deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete all the completed task",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // TODO: check this
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Long categoryId
    ){
        try {
            return new ResponseEntity<>(taskService.searchTasks(title,priority, completed, categoryId),
                    HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Nothing found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Nothing found",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sort")
    public ResponseEntity<?> sortByValues(
            @RequestParam(required = false, defaultValue = "taskId") String sortField,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction
    ){
        try {
            Sort sort = Sort.by(sortField);

            if (direction == Sort.Direction.DESC){
                sort = sort.descending();
            }

            return new ResponseEntity<>(taskService.getAllSortedTasks(sort), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // category
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories(){
        try {
            return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to get all the categories",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
