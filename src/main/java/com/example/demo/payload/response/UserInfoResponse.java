package com.example.demo.payload.response;

import java.util.List;

public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String firstName;
    private String lastName;

    private String cookie;
    private String cookieName;


    public UserInfoResponse(Long id, String username, String email, List<String> roles, String firstName, String lastName, String cookie,String cookieName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cookie = cookie;
        this.cookieName = cookieName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }
    public String getFirstname() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastname() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.username = lastName;
    }

    public String getCookie(){return cookie;}

    public void setCookie(String cookie) {this.cookie = cookie;}
    public String getCookieName(){return cookieName;}

    public void setCookieName(String cookieName) {this.cookieName = cookieName;}

}