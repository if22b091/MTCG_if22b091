package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.User;

public interface UserRepository {

    User searchByUsername(String username);

    void addUser(User user);

    void insertUsernameToDeck(String username);

    void insertUsernameToStats(String username);

    void editUser(String username, User user);
}
