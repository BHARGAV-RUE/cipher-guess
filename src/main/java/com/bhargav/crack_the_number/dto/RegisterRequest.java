package com.bhargav.crack_the_number.dto;

public class RegisterRequest {
    //Taking username
    private String username;
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    //Taking password
    private String password;
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    //Taking email
    private String email;

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
}
