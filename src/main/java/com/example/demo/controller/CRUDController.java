package com.example.demo.controller;

import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.payload.request.UpdateUserRequest;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.CRUDRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/crud")
public class CRUDController {
    @Autowired
    CRUDRepository crudRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtils;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String user) {
        try {
            List < User > users = new ArrayList< User >();

            if (user == null)
                crudRepository.findAll().forEach(users::add);

            if (users.isEmpty()) {
                return new ResponseEntity < > (HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity < > (users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity < > (null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity < User > getUserById(@PathVariable("id") long id) {
        Optional< User > userData = crudRepository.findById(id);

        if (userData.isPresent()) {
            return new ResponseEntity < > (userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity < > (HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity < User > updateUser(@PathVariable("id") long id, @RequestBody User data) {
        Optional < User > userData = crudRepository.findById(id);

        if (userData.isPresent()) {
            User _userData = userData.get();
            _userData.setEmail(data.getEmail());
            _userData.setFirstName(data.getFirstName());
            _userData.setLastName(data.getLastName());
            _userData.setPassword(data.getPassword());
            _userData.setRoles(data.getRoles());
            _userData.setUsername(data.getUsername());

            return new ResponseEntity < > (crudRepository.save(_userData), HttpStatus.OK);
        } else {
            return new ResponseEntity < > (HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody SignupRequest userRequest) {
        // Create a new user entity
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(encoder.encode(userRequest.getPassword()));

        // Assign roles to the user
        // Update user roles
        Set<String> strRoles = userRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles != null) {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }


        user.setRoles(roles);

        // Save the user to the database
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserAndRole(@PathVariable(value = "id") Long userId,
                                               @Valid @RequestBody UpdateUserRequest userDTO) {

        // Check if user exists
        User user = userRepository.findById(userId)

                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        // Update user details
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        // Update user roles
        Set<String> strRoles = userDTO.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles != null) {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);

        // Save updated user and roles
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User details and roles updated successfully!"));
    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity < HttpStatus > deleteUser(@PathVariable("id") long id) {
        try {
            crudRepository.deleteById(id);
            return new ResponseEntity < > (HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity < > (HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}