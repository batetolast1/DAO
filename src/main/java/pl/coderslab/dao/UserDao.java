package pl.coderslab.dao;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.entity.User;

import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO `users`(`email`, `username`, `password`) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY = "SELECT * FROM `users` WHERE `id` = ?";
    private static final String UPDATE_USER_QUERY =
            "UPDATE `users` SET `email` = ?, `username` = ?, `password` = ? WHERE `id` = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM `users` WHERE `id` = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM `users`";
    private static final String DELETE_ALL_USERS_QUERY = "DELETE FROM `users`";

    public User create(User user) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getCreateStatement(connection, user)) {
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private PreparedStatement getCreateStatement(Connection connection, User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getUserName());
        statement.setString(3, hashPassword(user.getPassword()));
        return statement;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User read(int userId) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getReadStatement(connection, userId)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return generateUserFrom(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private PreparedStatement getReadStatement(Connection connection, int userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(READ_USER_QUERY);
        statement.setInt(1, userId);
        return statement;
    }

    // TODO: 27.06.2020 add search for other fields?

    private User generateUserFrom(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        return user;
    }

    // TODO: 27.06.2020 add option to return value if update was successful?
    // stmt.getUpdateCount() method returns the number of rows affected.

    public void update(User user) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getUpdateStatement(connection, user)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private PreparedStatement getUpdateStatement(Connection connection, User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER_QUERY);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getUserName());
        statement.setString(3, hashPassword(user.getPassword()));
        statement.setInt(4, user.getId());
        return statement;
    }

    // TODO: 27.06.2020 add update for each field?

    public void delete(int userId) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getDeleteStatement(connection, userId)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private PreparedStatement getDeleteStatement(Connection connection, int userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY);
        statement.setInt(1, userId);
        return statement;
    }

    public void deleteAll() {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getDeleteAllStatement(connection)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private PreparedStatement getDeleteAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(DELETE_ALL_USERS_QUERY);
    }

    public User[] findAll() {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getFindAllStatement(connection)) {
            ResultSet resultSet = statement.executeQuery();
            User[] allUsers = new User[0];
            while (resultSet.next()) {
                allUsers = addToArray(generateUserFrom(resultSet), allUsers);
            }
            return allUsers;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private PreparedStatement getFindAllStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(FIND_ALL_USERS_QUERY);
    }

    private User[] addToArray(User user, User[] users) {
        User[] tempUsers = Arrays.copyOf(users, users.length + 1);
        tempUsers[users.length] = user;
        return tempUsers;
    }
}
