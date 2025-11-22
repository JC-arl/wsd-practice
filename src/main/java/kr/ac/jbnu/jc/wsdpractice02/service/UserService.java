package kr.ac.jbnu.jc.wsdpractice02.service;

import kr.ac.jbnu.jc.wsdpractice02.model.domain.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {

    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return userStore.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public User createUser(String username, String password) {
        Long id = idSeq.getAndIncrement();
        User user = new User(id, username, password);
        userStore.put(id, user);
        return user;
    }

    public Optional<User> updateUsername(Long id, String newUsername) {
        User user = userStore.get(id);
        if (user == null) {
            return Optional.empty();
        }
        user.setUsername(newUsername);
        return Optional.of(user);
    }

    public boolean changePassword(Long id, String newPassword) {
        User user = userStore.get(id);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        return true;
    }

    public boolean deleteById(Long id) {
        return userStore.remove(id) != null;
    }

    public int deleteAll() {
        int size = userStore.size();
        userStore.clear();
        return size;
    }
}
