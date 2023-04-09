package com.example.demo.controller;

import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.payload.request.SignupRequest;
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

    @PostMapping("/user")
    public ResponseEntity < ? > createUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getFirstName(), signUpRequest.getLastName());

        Set< String > strRoles = signUpRequest.getRole();
        Set <Role> roles = new HashSet< >();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
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
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @DeleteMapping("/user/{id}")
    public ResponseEntity < HttpStatus > deleteUser(@PathVariable("id") long id) {
        try {
            crudRepository.deleteById(id);
            return new ResponseEntity < > (HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity < > (HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}