package com.peerpay.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for user registration and profile operations.
 */
public class UserDTO {

    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phone;

    @NotBlank(message = "UPI ID is required")
    @Pattern(regexp = "^[\\w.-]+@[\\w]+$", message = "Invalid UPI ID format (e.g., user@upi)")
    private String upiId;

    private String password;

    public UserDTO() {
    }

    public UserDTO(String id, String name, String email, String phone, String upiId, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.upiId = upiId;
        this.password = password;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String upiId;
        private String password;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder upiId(String upiId) {
            this.upiId = upiId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, name, email, phone, upiId, password);
        }
    }
}
