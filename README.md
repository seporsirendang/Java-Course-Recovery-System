## About
This group project is a Java OOP-based build by Swing GUI for academic management system designed to support core administrative and academic workflows within an educational environment is developed.

## Built With
- Java (JDK 24)
- Java Swing & AWT
- JavaMail API
- JavaBeans Activation Framework
- iTextPDF library
- flat-file database

## Getting Started
Before you begin, ensure you have the following installed:
- JDK version 17 or higher (Built and tested using JDK 24).
- NetBeans IDE (Recommended) or IntelliJ IDEA / Eclipse.

## Installation
1. **Clone the repository**
```
git clone https://github.com/seporsirendang/Java-Course-Recovery_System.git/
```
2. **Open the project**
3. **Configure email settings**
- Open Email.java
- Ensure you have 2-Step Verification enabled on your Google Account
- Generate a 16-character App Password from your Google Security settings page
- Replace the placeholder password string inside the file with your email and generated 16-character app password:
  ```
  protected final String fromEmail = "mail@gmail.com";
  private final String password = "=app_password";
  ```
4. **Build and run**

## Notes
- Login information can be found in academic_staff_information.csv
