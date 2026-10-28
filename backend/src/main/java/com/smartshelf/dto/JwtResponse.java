package com.smartshelf.dto;

import com.smartshelf.models.Role;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;

    public JwtResponse() {}

    public JwtResponse(String token, String type, Long id, String username, String fullName, String email, Role role) {
        this.token = token;
        this.type = (type != null) ? type : "Bearer";
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private Role role;

        public Builder token(String token) { this.token = token; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder role(Role role) { this.role = role; return this; }

        public JwtResponse build() {
            return new JwtResponse(token, type, id, username, fullName, email, role);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
