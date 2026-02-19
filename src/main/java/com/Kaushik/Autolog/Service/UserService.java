package com.Kaushik.Autolog.Service;

import com.Kaushik.Autolog.Entity.Users;
import com.Kaushik.Autolog.Repository.UsersRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepo usersRepo;

    public UserService(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    public Users getUserById(Long id) {
        return usersRepo.findById(id).orElse(null);
    }

    public Users createUser(Users user) {
        return usersRepo.save(user);
    }

    public Users updateUser(Long id, Users updatedUser) {
        return usersRepo.findById(id).map(user -> {
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            return usersRepo.save(user);
        }).orElse(null);
    }

    public boolean deleteUser(Long id) {
        return usersRepo.findById(id).map(user -> {
            usersRepo.delete(user);
            return true;
        }).orElse(false);
    }
}
