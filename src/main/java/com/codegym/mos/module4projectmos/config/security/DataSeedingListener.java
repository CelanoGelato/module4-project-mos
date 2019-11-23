package com.codegym.mos.module4projectmos.config.security;

import com.codegym.mos.module4projectmos.model.entity.Privilege;
import com.codegym.mos.module4projectmos.model.entity.Role;
import com.codegym.mos.module4projectmos.model.entity.User;
import com.codegym.mos.module4projectmos.service.PrivilegeService;
import com.codegym.mos.module4projectmos.service.RoleService;
import com.codegym.mos.module4projectmos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Component
public class DataSeedingListener {
    private boolean alreadySetup = false;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void onApplicationEvent() {
        if (alreadySetup)
            return;
        Privilege readNote
                = createPrivilegeIfNotFound("SONG_READ");
        Privilege writeNote
                = createPrivilegeIfNotFound("SONG_WRITE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readNote, writeNote);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readNote));
        createAccounts();
        alreadySetup = true;
    }

    private Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeService.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeService.save(privilege);
        }
        return privilege;
    }

    private void createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleService.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleService.save(role);
        }
    }

    private void createAccounts() {
        // Admin account
        String username = "admin";
        String password;
        String firstName;
        String lastName = "Lambda";
        if (!userService.findByUsername(username).isPresent()) {
            password = passwordEncoder.encode("Lambda123456");
            firstName = "Admin";
            HashSet<Role> roles1 = new HashSet<>();
            roles1.add(roleService.findByName("ROLE_USER"));
            roles1.add(roleService.findByName("ROLE_ADMIN"));
            User admin = new User(username, password, roles1);
            admin.setGender(true);
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            userService.save(admin);
        }

        // Member account
        username = "member";
        if (!userService.findByUsername(username).isPresent()) {
            password = passwordEncoder.encode("Lambda123456");
            firstName = "Member";
            HashSet<Role> roles2 = new HashSet<>();
            roles2.add(roleService.findByName("ROLE_USER"));
            User member = new User(username, password, roles2);
            member.setGender(true);
            member.setFirstName(firstName);
            member.setLastName(lastName);
            userService.save(member);
        }
    }
}