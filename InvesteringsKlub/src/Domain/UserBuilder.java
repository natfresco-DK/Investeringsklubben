package Domain;

import java.util.Date;

public class UserBuilder {
    private int userId;
    private String fullName;
    private String email;
    private Date birthDate;
    private int initialCashDKK;
    private Date createdAt;
    private Date lastUpdated;

    public UserBuilder() {
        this.createdAt = new Date();
        this.lastUpdated = new Date();
    }

    public UserBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public UserBuilder setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public UserBuilder setInitialCashDKK(int initialCashDKK) {
        this.initialCashDKK = initialCashDKK;
        return this;
    }

    public UserBuilder setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public User build() {
        return new User(userId, fullName, email, birthDate, initialCashDKK, createdAt, lastUpdated);
    }
}
