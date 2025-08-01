package com.project.system.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.system.enums.input.UserPermission;
import com.project.system.enums.input.UserRegion;
import com.project.system.enums.input.UserRole;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    
    @Enumerated(EnumType.STRING)	
    @Column(nullable = false)
    private UserRole userRole;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<UserPermission> permissions = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "function_id", referencedColumnName = "functionId")
    private Function userFunction;
    
	@ManyToOne
    @JoinColumn(name = "occupation_id", referencedColumnName = "occupationId")
    private Occupation userOccupation;
	
	@ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "departmentId")
    private Department userDepartment;
	
    private String userTel;
    
    @Column(name = "primeiro_acesso")
    private boolean primeiroAcesso = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRegion userRegion;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private Boolean userLocked = false;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private Boolean userInactive = false;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private Boolean userSuspended = false;

    @Column(nullable = false, columnDefinition = "bit default 1")
    private Boolean userActive = true;

    private LocalDateTime userRegistrationDate = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_allowed_days", joinColumns = {@JoinColumn(name = "user_id")})
    @Column(name = "day")
    private List<String> allowedDays;

    private LocalTime startTime;
    private LocalTime endTime;

    public User() {}

    public User(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userRegistrationDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (userRole != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
        }
        if (permissions != null) {
            permissions.forEach(permission -> 
                authorities.add(new SimpleGrantedAuthority(permission.name()))
            );
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.userLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

	@Override
	public boolean isEnabled() {
	    return Boolean.TRUE.equals(userActive)
	        && !Boolean.TRUE.equals(userInactive)
	        && !Boolean.TRUE.equals(userSuspended)
	        && !Boolean.TRUE.equals(userLocked);
	}
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public UserRole getUserRole() {
	    return userRole;
	}

	public void setUserRole(UserRole userRole) {
	    this.userRole = userRole;
	}

    public Boolean getUserLocked() {
        return userLocked;
    }

    public void setUserLocked(Boolean userLocked) {
        this.userLocked = userLocked;
    }

    public boolean isUserLocked() {
        return this.userLocked;
    }

    public Boolean getUserActive() {
        return userActive;
    }

    public void setUserActive(Boolean userActive) {
        this.userActive = userActive;
    }
    
    // ** Método que faltava **
    public boolean isUserActive() {
        return Boolean.TRUE.equals(this.userActive);
    }

    public boolean isUserInactive() {
        return Boolean.TRUE.equals(this.userInactive);
    }
    
    // ** Método que faltava **
    public void setUserInactive(Boolean userInactive) {
        this.userInactive = userInactive;
    }

    public boolean isUserSuspended() {
        return Boolean.TRUE.equals(this.userSuspended);
    }
    
    // ** Método que faltava **
    public void setUserSuspended(Boolean userSuspended) {
        this.userSuspended = userSuspended;
    }

    public LocalDateTime getUserRegistrationDate() {
        return userRegistrationDate;
    }

    public void setUserRegistrationDate(LocalDateTime userRegistrationDate) {
        this.userRegistrationDate = userRegistrationDate;
    }

    public List<String> getAllowedDays() {
        return this.allowedDays;
    }

    public void setAllowedDays(List<String> allowedDays) {
        this.allowedDays = allowedDays;
    }

    public LocalTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Occupation getUserOccupation() {
        return userOccupation;
    }

    public void setUserOccupation(Occupation userOccupation) {
        this.userOccupation = userOccupation;
    }
    
    public Function getUserFunction() {
        return userFunction;
    }

    public void setUserFunction(Function userFunction) {
        this.userFunction = userFunction;
    }
    
    public Department getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(Department userDepartment) {
        this.userDepartment = userDepartment;
    }
    
    public boolean isPrimeiroAcesso() {
		return primeiroAcesso;
	}

	public void setPrimeiroAcesso(boolean primeiroAcesso) {
		this.primeiroAcesso = primeiroAcesso;
	}

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public UserRegion getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(UserRegion userRegion) {
        this.userRegion = userRegion;
    }

    public boolean isOutsideAllowedTime() {
        if (this.startTime == null && this.endTime == null) {
            return false;
        }
        LocalTime now = LocalTime.now();
        return (this.startTime != null && now.isBefore(this.startTime)) ||
               (this.endTime != null && now.isAfter(this.endTime));
    }

	public Set<UserPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<UserPermission> permissions) {
		this.permissions = permissions;
	}

	public Boolean getUserInactive() {
		return userInactive;
	}

	public Boolean getUserSuspended() {
		return userSuspended;
	}

}
