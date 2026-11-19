package org.csillagkoz.model;

public class User {
    private String username;
    private String password;
    private String role; // "USER" vagy "ADMIN"

    // Opcionálisan elmentett személyes adatok
    private String lastName;
    private String firstName;
    private String birthDate;
    private String idType;
    private String passportNumber;
    private String email;
    private String phone;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean hasPersonalData() {
        return lastName != null && !lastName.isEmpty()
                && firstName != null && !firstName.isEmpty();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
