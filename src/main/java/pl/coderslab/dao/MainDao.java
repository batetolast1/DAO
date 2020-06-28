package pl.coderslab.dao;

import pl.coderslab.entity.User;

import java.util.Arrays;

public class MainDao {

    public static void main(String[] args) {
        UserDao userDao = new UserDao();

        System.out.println("Creating new users");
        User user1 = new User();
        user1.setEmail("user1@gmail.com");
        user1.setUserName("user1");
        user1.setPassword("password1");
        user1 = userDao.create(user1);
        System.out.println(user1);

        User user2 = new User();
        user2.setEmail("user2@gmail.com");
        user2.setUserName("user2");
        user2.setPassword("password2");
        user2 = userDao.create(user2);
        System.out.println(user2);

        User user3 = new User();
        user3.setEmail("user3@gmail.com");
        user3.setUserName("user1");
        user3.setPassword("password3");
        user3 = userDao.create(user3);
        System.out.println(user3);

        System.out.println("Creating invalid user:");
        User user4 = new User();
        user4 = userDao.create(user4);
        System.out.println(user4);

        System.out.println("\nReading user");
        System.out.println(userDao.read(1));
        System.out.println("Reading non-existing user");
        System.out.println(userDao.read(4));

        System.out.println("\nReading user by e-mail");
        System.out.println(userDao.readByEmail("user1@gmail.com"));
        System.out.println("\nReading user with e-mail not existing in database");
        System.out.println(userDao.readByEmail("user11@gmail.com"));

        System.out.println("\nReading user by username");
        System.out.println(Arrays.deepToString(userDao.readByUserName("user1")));
        System.out.println("\nReading user with e-mail not existing in database");
        System.out.println(Arrays.deepToString(userDao.readByUserName("user3")));

        System.out.println("\nChanging all user data");
        System.out.println("User to update:");
        user1 = userDao.read(1);
        System.out.println(user1);
        user1.setEmail("user1@interia.pl");
        user1.setUserName("USER1");
        user1.setPassword("new_password1");
        System.out.println("User to update with changed data");
        System.out.println(user1);
        System.out.println(userDao.update(user1));
        user1 = userDao.read(1);
        System.out.println("User after update");
        System.out.println(user1);

        System.out.println("\nChanging user data without password");
        System.out.println("User to update:");
        user2 = userDao.read(2);
        System.out.println(user2);
        user2.setEmail("user2@yahoo.com");
        user2.setUserName("UsSSSser2");
        System.out.println("User to update with changed data");
        System.out.println(user2);
        System.out.println(userDao.update(user2));
        user2 = userDao.read(2);
        System.out.println("User after update");
        System.out.println(user2);

        System.out.println("\nChecking password match");
        System.out.println(user1.checkPasswordMatch("new_password1"));
        System.out.println(user1.checkPasswordMatch("wrong_password"));

        System.out.println("\nPrinting all users");
        User[] allUsers = userDao.findAll();
        for (User user : allUsers) {
            System.out.println(user);
        }

        System.out.println("\nDeleting user");
        System.out.println(userDao.delete(1));
        System.out.println(userDao.read(1));

        System.out.println("\nPrinting all users");
        allUsers = userDao.findAll();
        for (User user : allUsers) {
            System.out.println(user);
        }

        System.out.println("\nDeleting all users");
        System.out.println(userDao.deleteAll());
        allUsers = userDao.findAll();
        System.out.println("Printing all users");
        for (User user : allUsers) {
            System.out.println(user);
        }
    }
}
