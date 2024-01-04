package com.binarybricks.coinbit;

public class AuthenticationValidator {
    public static String validateEmail(String emailString){
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            return "";
        }

        return "Invalid email address";
    }

    public static String validatePassword(String passwordString, final int maxLength){
        if(passwordString.equals(""))
            return "Password cannot be empty";
        else if(passwordString.length() < maxLength)
            return "Password must have more then " + maxLength + " characters";

        return "";
    }

    public static String comparePasswords(String passwordA, String passwordB){
        if(passwordA.equals(passwordB))
            return "";

        return "Passwords do not match";
    }
}
