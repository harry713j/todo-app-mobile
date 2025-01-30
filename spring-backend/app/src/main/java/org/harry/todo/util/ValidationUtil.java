package org.harry.todo.util;

public class ValidationUtil {
    public static boolean validateUsername(String username){
        // just checking username length must be 4 or more
        if (username == null || username.length() < 4){
            return false;
        }

        String regex = "^[a-zA-Z0-9]+$";
        return username.matches(regex);
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
            return false;
        }

        String regex = "^[a-zA-Z0-9@#$%&_-]+$";
        return password.matches(regex);
    }
}
