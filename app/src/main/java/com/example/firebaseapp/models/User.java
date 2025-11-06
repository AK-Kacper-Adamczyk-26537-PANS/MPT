package com.example.firebaseapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userId;
    private String firstName;
    private String lastName;
    private String major;
    private int age;
    private String phone;
    private String email;
    private String role; // np. "member", "admin", "prezes"
    private String function; // Funkcja w kole

    // Pusty konstruktor wymagany przez Firebase
    public User() {}

    // Konstruktor z siedmioma parametrami (bez userId i function)
    public User(String firstName, String lastName, String major, int age, String phone, String email, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    // Opcjonalnie: Konstruktor z ośmioma parametrami, jeśli potrzebujesz funkcji
    public User(String firstName, String lastName, String major, int age, String phone, String email, String role, String function) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.function = function;
    }

    // Konstruktor używany podczas parcelowania
    protected User(Parcel in) {
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        major = in.readString();
        age = in.readInt();
        phone = in.readString();
        email = in.readString();
        role = in.readString();
        function = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) { return new User(in); }

        @Override
        public User[] newArray(int size) { return new User[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(major);
        dest.writeInt(age);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(role);
        dest.writeString(function);
    }

    // Gettery i Settery

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFunction() { return function; }
    public void setFunction(String function) { this.function = function; }
}
