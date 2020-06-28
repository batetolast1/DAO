package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;

public class User {
    private int id;
    private String email;
    private String userName;
    private String password;

    public static class Builder {
        private int id;
        private String email;
        private String userName;
        private String password;

        public Builder(int id) {
            this.id = id;
        }
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.email = this.email;
            user.userName = this.userName;
            user.password = this.password;
            return user;
        }
    }

    private User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public boolean checkPasswordMatch(String passwordToCheck) {
        return BCrypt.checkpw(passwordToCheck, password);
    }
}
