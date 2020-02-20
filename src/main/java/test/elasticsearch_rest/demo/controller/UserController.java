package test.elasticsearch_rest.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import test.elasticsearch_rest.demo.dao.user.UserDao;
import test.elasticsearch_rest.demo.model.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired @Qualifier("http-final")
    UserDao userRepo;

    @PostMapping("/create")
    public ResponseEntity<APIResponse> createUser(@RequestBody @Valid User newInstance) {

        userRepo.create(newInstance);
        
        return ResponseEntity.status(HttpStatus.OK).body(new APIResponse("User successfully created!"));

    }

    @GetMapping("")
    public List<User> listAllUsers(){

        return userRepo.findAll();

    }

    @GetMapping("/id/{id}")
    public User findUser(@PathVariable(value="id") String id) {

        return userRepo.findById(id);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable(value="id") String id, @RequestBody @Valid User transientObject) {

        // TODO: Turn Id - Object check an aspect
        if(id.equals(transientObject.getUsername())) {

            userRepo.update(transientObject);

            return ResponseEntity.status(HttpStatus.OK).body("User successfully updated!");

        } else 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id doesn't match payload!");
            
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value="id") String id, @RequestBody @Valid User persistentObject) {

        if(id.equals(persistentObject.getUsername())) {

            userRepo.delete(persistentObject);

            return ResponseEntity.status(HttpStatus.OK).body("User successfully deleted!");

        } else 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id doesn't match payload!");

    }

}