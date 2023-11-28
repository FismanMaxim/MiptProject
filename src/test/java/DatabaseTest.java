import Entities.Company;
import Entities.User;
import database.Database;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private static Database database;

    @BeforeAll
    public static void setUp() throws SQLException {
        // Initialize the database connection before running the tests

        database = new Database(DriverManager.getConnection("jdbc:postgresql" +
                        "://cornelius.db.elephantsql.com:5432/hmtdjque",
                "hmtdjque",
                "mW9O7Imtz3eqjtvVolLGZ4gWlC9VuKMh"));
        database.connection.prepareStatement("DROP table users;DROP table " +
                "companies;CREATE TABLE users (\n" +
                "                       id SERIAL PRIMARY KEY,\n" +
                "                       name VARCHAR(255),\n" +
                "                       money INT,\n" +
                "                       shares hstore\n" +
                ");CREATE TABLE companies (\n" +
                "                           id SERIAL PRIMARY KEY,\n" +
                "                           users INT[],\n" +
                "                           name VARCHAR(255) NOT NULL,\n" +
                "                           key_shareholder_threshold INT NOT NULL,\n" +
                "                           vacant_shares INT NOT NULL,\n" +
                "                           total_shares INT NOT NULL,\n" +
                "                           money INT NOT NULL,\n" +
                "                           share_price INT NOT NULL\n" +
                ");").execute();
    }

    @AfterAll
    public static void tearDown() {
        // Close the database connection after running all tests
        // You might want to move this to an @AfterEach method if you're using JUnit 4
        database.dropConnection();
    }

    @AfterEach
    public void clearing() {
        try {
            database.connection.prepareStatement("delete from users where id != " +
                    "-3").execute();
            database.connection.prepareStatement("delete from companies where id != " +
                    "-3").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInMemoryUserGetById() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>());
        database.new InMemoryUser().create(testUser);

        // Retrieve the user by ID
        User retrievedUser = database.new InMemoryUser().getById(1);

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertEquals(testUser.getId(), retrievedUser.getId());
        assertEquals(testUser.getUserName(), retrievedUser.getUserName());
        assertEquals(testUser.getMoney(), retrievedUser.getMoney());
        assertEquals(testUser.getShares(), retrievedUser.getShares());
    }

    @Test
    public void testInMemoryUserCreate() {
        // Create a user and add it to the database
        User testUser = new User(2, "TestUser2", 200.0, new HashMap<>());
        database.new InMemoryUser().create(testUser);

        // Retrieve the user by ID
        User retrievedUser = database.new InMemoryUser().getById(2);

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertEquals(testUser.getId(), retrievedUser.getId());
        assertEquals(testUser.getUserName(), retrievedUser.getUserName());
        assertEquals(testUser.getMoney(), retrievedUser.getMoney());
        assertEquals(testUser.getShares(), retrievedUser.getShares());
    }

    // Add similar tests for update and delete methods

    @Test
    public void testInMemoryCompanyGetById() {
        // Create a company and add it to the database
        Company testCompany = new Company(1, "TestCompany", 100, 50, 0.5F,
                1000, 10, new HashSet<>());
        database.new InMemoryCompany().create(testCompany);

        // Retrieve the company by ID
        Company retrievedCompany = database.new InMemoryCompany().getById(1);

        // Check if the retrieved company is not null
        assertNotNull(retrievedCompany);

        // Check if the retrieved company has the correct properties
        assertEquals(testCompany.getId(), retrievedCompany.getId());
        assertEquals(testCompany.getCompanyName(), retrievedCompany.getCompanyName());
        assertEquals(testCompany.getTotalShares(), retrievedCompany.getTotalShares());
        assertEquals(testCompany.getVacantShares(), retrievedCompany.getVacantShares());
        assertEquals(testCompany.getKeyShareholderThreshold(), retrievedCompany.getKeyShareholderThreshold());
        assertEquals(testCompany.getMoney(), retrievedCompany.getMoney());
        assertEquals(testCompany.getSharePrice(), retrievedCompany.getSharePrice());
        assertEquals(testCompany.getUsers(), retrievedCompany.getUsers());
    }

    @Test
    public void testInMemoryCompanyCreate() {
        // Create a company and add it to the database
        Company testCompany = new Company(2, "TestCompany2", 200, 100, 0.7F,
                1500, 15, new HashSet<>());
        database.new InMemoryCompany().create(testCompany);

        // Retrieve the company by ID
        Company retrievedCompany = database.new InMemoryCompany().getById(2);

        // Check if the retrieved company is not null
        assertNotNull(retrievedCompany);

        // Check if the retrieved company has the correct properties
        assertEquals(testCompany.getId(), retrievedCompany.getId());
        assertEquals(testCompany.getCompanyName(), retrievedCompany.getCompanyName());
        assertEquals(testCompany.getTotalShares(), retrievedCompany.getTotalShares());
        assertEquals(testCompany.getVacantShares(), retrievedCompany.getVacantShares());
        assertEquals(testCompany.getKeyShareholderThreshold(), retrievedCompany.getKeyShareholderThreshold());
        assertEquals(testCompany.getMoney(), retrievedCompany.getMoney());
        assertEquals(testCompany.getSharePrice(), retrievedCompany.getSharePrice());

        // Check if the retrieved company users is an empty set
        assertNotNull(retrievedCompany.getUsers());
        assertTrue(retrievedCompany.getUsers().isEmpty());
    }

    @Test
    public void testInMemoryCompanyAddUsersToCompany() {
        // Create a company and add it to the database
        Company testCompany = new Company(3, "TestCompany3", 300, 150, 0.8F,
                2000, 20, new HashSet<>());
        database.new InMemoryCompany().create(testCompany);

        // Create users and add them to the company
        User user1 = new User(101, "User101", 100.0, new HashMap<>());
        User user2 = new User(102, "User102", 150.0, new HashMap<>());

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        database.new InMemoryCompany().addUsersToCompany(3, users);
        database.new InMemoryUser().create(user1);
        database.new InMemoryUser().create(user2);
        // Retrieve the updated company
        Company updatedCompany = database.new InMemoryCompany().getById(3);

        // Check if the users were added to the company
        assertNotNull(updatedCompany);
        assertEquals(2, updatedCompany.getUsers().size());
        assertTrue(updatedCompany.getUsers().contains(user1));
        assertTrue(updatedCompany.getUsers().contains(user2));
    }
}
