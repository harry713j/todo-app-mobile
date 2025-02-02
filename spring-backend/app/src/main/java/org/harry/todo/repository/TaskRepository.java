package org.harry.todo.repository;

import org.harry.todo.entities.Priority;
import org.harry.todo.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:title IS NULL OR t.title ILIKE CONCAT('%', :title, '%')) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Task> searchTasksByCriteria(@Param("title") String title,
                                      @Param("priority") Priority priority,
                                      @Param("completed") Boolean completed,
                                      @Param("categoryId") Long categoryId);
}
