package springdev.scm.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// public class User {
public class User implements UserDetails {

    @Id                                                                                                                                                                                                                                                                                                      
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Getter(value = AccessLevel.NONE)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "about", length = 500)
    private String about;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Getter(value = AccessLevel.NONE)
    @Column(name = "enabled", nullable = true)
    private boolean enabled = true; 

    @Column(name = "email_verified", nullable = true)
    private boolean emailVerified = false;

    @Column(name = "phone_number_verified", nullable = true)
    private boolean phoneNumberVerified = false;

    // :> assiging "SELF" as default
    @Enumerated(value = EnumType.STRING)
    @Column(name = "provider")
    private Providers provider = Providers.SELF;

    @Column(name = "provider_user_id")
    private String providerUserId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> roles =  roleList.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());

        return roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() { 
        return this.enabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}
