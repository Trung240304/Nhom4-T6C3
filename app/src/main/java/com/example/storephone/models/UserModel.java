package com.example.storephone.models;

public class UserModel {
    private String name;
    private String email;
    private String password; // Lưu ý: Không nên lưu mật khẩu như plaintext trong thực tế
    private String phoneNumber;
    private String address;
    private String profileImageUrl;

    // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    public UserModel() {}

    // Constructor for registration
    public UserModel(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Full constructor
    public UserModel(String name, String email, String phoneNumber, String address, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}