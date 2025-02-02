package org.harry.todo.util;

import org.harry.todo.dto.request.TaskRequestDTO;

public class ValidationUtil {
    public static boolean validateUsername(String username){
        // just checking username length must be 4 or more
        if (username == null || username.length() < 4){
            return true;
        }

        String regex = "^[a-zA-Z0-9]+$";
        return !username.matches(regex);
    }

    public static boolean validateEmail(String email){
        if (email == null){
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    public static boolean validatePassword(String password){
        if (password == null || password.length() < 8){
            return true;
        }

        String regex = "^[a-zA-Z0-9@#$%&_-]+$";
        return !password.matches(regex);
    }

    public static void validateTask(TaskRequestDTO taskRequestDTO){
        if (taskRequestDTO.getTitle() == null || taskRequestDTO.getCreatedAt() == null
                || taskRequestDTO.getPriority() == null || taskRequestDTO.getTargetDate() == null
                || taskRequestDTO.getCategoryId() == null){
            throw new RuntimeException("Some missing details");
        }

        if (taskRequestDTO.getCreatedAt().isAfter(taskRequestDTO.getTargetDate())){
            throw new RuntimeException("Invalid date and time details");
        }
    }
}
