package bikram.businessmanager.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private String employeeId;
    private boolean isActive;
    private LocalDateTime lastLogin;

    public User(){}
}
