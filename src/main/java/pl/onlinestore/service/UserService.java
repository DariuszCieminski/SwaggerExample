package pl.onlinestore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.onlinestore.dao.UserDao;
import pl.onlinestore.exception.NotFoundException;
import pl.onlinestore.model.User;
import pl.onlinestore.model.enums.Role;

@Service
public class UserService implements EntityService<User> {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public User getById(Long id) {
        return userDao.findById(id).orElseThrow(() -> new NotFoundException("There is no user with id: " + id));
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        userDao.findAll().forEach(users::add);
        return users;
    }

    @Override
    public User add(User object) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + Role.MANAGER.name()))) {
            object.setRoles(Collections.singleton(Role.USER));
        }
        object.setPassword(passwordEncoder.encode(object.getPassword()));
        return userDao.save(object);
    }

    @Override
    public User update(User object) {
        if (object.getId() == null || !userDao.existsById(object.getId())) {
            throw new NotFoundException("User doesn't exist.");
        }

        if (object.getPassword() != null) {
            object.setPassword(passwordEncoder.encode(object.getPassword()));
        } else {
            object.setPassword(getById(object.getId()).getPassword());
        }

        return userDao.save(object);
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        userDao.delete(user);
    }
}