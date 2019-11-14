package com.codegym.mos.module4projectmos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode()
@NoArgsConstructor
@JsonIgnoreProperties(value = {"enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"}, allowGetters = true, ignoreUnknown = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+([a-zA-Z0-9]([_\\- ])[a-zA-Z0-9])*[a-zA-Z0-9]+${8,}")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull
    private Boolean gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Pattern(regexp = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- ()]*${10,13}")
    private String phoneNumber;

    @Email
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    public User(String username, String password, Collection<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String password, String firstName, String lastName, Boolean gender, Date birthDate, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
