package pl.coderslab.dao;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.entity.User;

import java.sql.*;
import java.util.Arrays;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO `users`(`email`, `username`, `password`) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY = "SELECT * FROM `users` WHERE `id` = ?";
    private static final String READ_USER_BY_EMAIL_QUERY = "SELECT * FROM `users` WHERE `email` = ?";
    private static final String READ_USER_BY_USERNAME = "SELECT * FROM `users` WHERE `username` = ?";
    private static final String UPDATE_USER_QUERY =
            "UPDATE `users` SET `email` = ?, `username` = ?, `password` = ? WHERE `id` = ?";
    private static final String UPDATE_USER_WITHOUT_PASSWORD_QUERY =
            "UPDATE `users` SET `email` = ?, `username` = ? WHERE `id` = ?";
    private static final String GET_PASSWORD_QUERY = "SELECT `password` FROM `users` WHERE `id` = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM `users` WHERE `id` = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM `users`";
    private static final String DELETE_ALL_USERS_QUERY = "DELETE FROM `users`";

    public User create(User user) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getCreateStatement(connection, user.getEmail(), user.getUserName(), user.getPassword())) {
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

    private PreparedStatement getCreateStatement(Connection connection, String email, String userName, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, email);
        statement.setString(2, userName);
        statement.setString(3, hashPassword(password));
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

    private User generateUserFrom(ResultSet resultSet) throws SQLException {
        return new User.Builder(resultSet.getInt("id"))
                .withEmail(resultSet.getString("email"))
                .withUserName(resultSet.getString("username"))
                .withPassword(resultSet.getString("password"))
                .build();
        /*User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        return user;*/
    }

    public User readByEmail(String email) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getReadByEmailStatement(connection, email)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return generateUserFrom(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private PreparedStatement getReadByEmailStatement(Connection connection, String email) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(READ_USER_BY_EMAIL_QUERY);
        statement.setString(1, email);
        return statement;
    }

    public User[] readByUserName(String userName) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getReadByUserNameStatement(connection, userName)) {
            ResultSet resultSet = statement.executeQuery();
            User[] users = new User[0];
            while (resultSet.next()) {
                users = addToArray(generateUserFrom(resultSet), users);
            }
            return users;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private PreparedStatement getReadByUserNameStatement(Connection connection, String userName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(READ_USER_BY_USERNAME);
        statement.setString(1, userName);
        return statement;
    }

    public int update(User user) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getUpdateStatement(connection, user)) {
            statement.executeUpdate();
            return statement.getUpdateCount();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    private PreparedStatement getUpdateStatement(Connection connection, User user) throws SQLException {
        if (isPasswordUnchanged(user)) {
            return getUpdateStatementWithoutPassword(connection, user.getId(), user.getEmail(), user.getUserName());
        } else {
            return getFullUpdateStatement(connection, user.getId(), user.getEmail(), user.getUserName(), user.getPassword());
        }
    }

    private PreparedStatement getFullUpdateStatement(Connection connection, int id, String email, String userName, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER_QUERY);
        statement.setString(1, email);
        statement.setString(2, userName);
        statement.setString(3, hashPassword(password));
        statement.setInt(4, id);
        return statement;
    }

    private PreparedStatement getUpdateStatementWithoutPassword(Connection connection, int id, String email, String userName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER_WITHOUT_PASSWORD_QUERY);
        statement.setString(1, email);
        statement.setString(2, userName);
        statement.setInt(3, id);
        return statement;
    }

    private boolean isPasswordUnchanged(User userToUpdate) {
        return userToUpdate.getPassword().equals(getPasswordFromDatabase(userToUpdate.getId()));
    }

    private String getPasswordFromDatabase(int userId) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getPasswordStatement(connection, userId)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private PreparedStatement getPasswordStatement(Connection connection, int userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_PASSWORD_QUERY);
        statement.setInt(1, userId);
        return statement;
    }

    public int delete(int userId) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getDeleteStatement(connection, userId)) {
            statement.executeUpdate();
            return statement.getUpdateCount();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    private PreparedStatement getDeleteStatement(Connection connection, int userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY);
        statement.setInt(1, userId);
        return statement;
    }

    public int deleteAll() {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = getDeleteAllStatement(connection)) {
            statement.executeUpdate();
            return statement.getUpdateCount();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
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
