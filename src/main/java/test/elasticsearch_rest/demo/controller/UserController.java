package test.elasticsearch_rest.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import test.elasticsearch_rest.demo.dao.user.UserDao;
import test.elasticsearch_rest.demo.model.User;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    @Qualifier("final")
    UserDao userRepo;

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody @Valid User newInstance) {
        userRepo.create(newInstance);
        return ResponseEntity.status(HttpStatus.OK).body("User successfully created!");
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
        if(id.equals(transientObject.getUsername())) userRepo.update(transientObject);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id doesn't match payload!");
        return ResponseEntity.status(HttpStatus.OK).body("User successfully updated!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value="id") String id) {
        User persistentObject = new User();
        persistentObject.setUsername(id);
        userRepo.delete(persistentObject);
        return ResponseEntity.status(HttpStatus.OK).body("User successfully deleted!");
    }

}