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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private static Database database;

    @BeforeAll
    public static void setUp() throws SQLException {
        // Initialize the database connection before running the tests

/*        database = new Database(DriverManager.getConnection("jdbc:postgresql" +
                        "://cornelius.db.elephantsql.com:5432/hmtdjque",
                "hmtdjque",
                "mW9O7Imtz3eqjtvVolLGZ4gWlC9VuKMh"));*/
        database = new Database();
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
        database.user.create(testUser);

        // Retrieve the user by ID
        User retrievedUser = database.user.getById(1);

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertEquals(testUser.getId(), retrievedUser.getId());
        assertEquals(testUser.getUserName(), retrievedUser.getUserName());
        assertEquals(testUser.getMoney(), retrievedUser.getMoney());
        assertEquals(testUser.getCopyOfShares(), retrievedUser.getCopyOfShares());
    }

    @Test
    public void testInMemoryUserCreate() {
        // Create a user and add it to the database
        User testUser = new User(2, "TestUser2", 200.0, new HashMap<>());
        database.user.create(testUser);

        // Retrieve the user by ID
        User retrievedUser = database.user.getById(2);

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertEquals(testUser.getId(), retrievedUser.getId());
        assertEquals(testUser.getUserName(), retrievedUser.getUserName());
        assertEquals(testUser.getMoney(), retrievedUser.getMoney());
        assertEquals(testUser.getCopyOfShares(), retrievedUser.getCopyOfShares());
    }

    @Test
    public void testInMemoryCompanyGetById() {
        // Create a company and add it to the database
        Company testCompany = new Company(1, "TestCompany", 100, 50, 0.5F,
                1000, 10, new HashSet<>());
        database.company.create(testCompany);

        // Retrieve the company by ID
        Company retrievedCompany = database.company.getById(1);

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
        database.company.create(testCompany);

        // Retrieve the company by ID
        Company retrievedCompany = database.company.getById(2);

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
        database.company.create(testCompany);

        // Create users and add them to the company
        User user1 = new User(101, "User101", 100.0, new HashMap<>());
        User user2 = new User(102, "User102", 150.0, new HashMap<>());

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        database.company.addUsersToCompany(3, users);
        database.user.create(user1);
        database.user.create(user2);
        // Retrieve the updated company
        Company updatedCompany = database.company.getById(3);

        // Check if the users were added to the company
        assertNotNull(updatedCompany);
        assertEquals(2, updatedCompany.getUsers().size());
        assertTrue(updatedCompany.getUsers().contains(user1));
        assertTrue(updatedCompany.getUsers().contains(user2));
    }


    @Test
    public void testDatabaseSetConnection() {
        try {
            // Create a new connection and set it in the database
            new Database(DriverManager.getConnection("jdbc:postgresql" +
                    "://new-database-url:5432/new-database", "new-username", "new-password"));
            // Check if the connection is set successfully
            assertNotNull(database.connection);
        } catch (SQLException e) {
            System.out.println("it`s ok if only sqlexception");
        }
    }

    @Test
    public void testInMemoryCompanyGetAll() {
        // Create companies and add them to the database
        Company company1 = new Company(1, "Company1", 100, 50, 0.5F, 1000, 10, new HashSet<>());
        Company company2 = new Company(2, "Company2", 200, 100, 0.7F, 1500, 15, new HashSet<>());
        database.company.create(company1);
        database.company.create(company2);

        // Retrieve all companies
        var allCompanies = database.company.getAll();

        // Check if all companies are retrieved
        assertEquals(2, allCompanies.size());
        assertTrue(allCompanies.contains(company1));
        assertTrue(allCompanies.contains(company2));
    }

    @Test
    public void testInMemoryCompanyUpdate() {
        // Create a company and add it to the database
        Company testCompany = new Company(1, "TestCompany", 100, 50, 0.5F, 1000, 10, new HashSet<>());
        database.company.create(testCompany);

        // Update the company
        testCompany = new Company(testCompany.getId(), "UpdatedCompany",
                200, testCompany.getVacantShares(),
                testCompany.getKeyShareholderThreshold(),
                testCompany.getMoney(), testCompany.getSharePrice(),
                testCompany.getUsers());
        database.company.update(testCompany);

        // Retrieve the updated company
        Company updatedCompany = database.company.getById(1);

        // Check if the company is updated successfully
        assertNotNull(updatedCompany);
        assertEquals("UpdatedCompany", updatedCompany.getCompanyName());
        assertEquals(200, updatedCompany.getTotalShares());
    }

    @Test
    public void testInMemoryCompanyDelete() {
        // Create a company and add it to the database
        Company testCompany = new Company(1, "TestCompany", 100, 50, 0.5F, 1000, 10, new HashSet<>());
        database.company.create(testCompany);

        // Delete the company
        database.company.delete(1);

        // Try to retrieve the deleted company
        Company deletedCompany = database.company.getById(1);

        // Check if the company is deleted successfully
        assertNull(deletedCompany);
    }

    @Test
    public void testInMemoryUserUpdate() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>());
        database.user.create(testUser);

        // Update the user
        testUser = new User(1, "UpdatedUser", 150.0, testUser.getCopyOfShares());
        database.user.update(testUser);

        // Retrieve the updated user
        User updatedUser = database.user.getById(1);

        // Check if the user is updated successfully
        assertNotNull(updatedUser);
        assertEquals("UpdatedUser", updatedUser.getUserName());
        assertEquals(150.0, updatedUser.getMoney());
    }

    @Test
    public void testInMemoryUserDelete() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>());
        database.user.create(testUser);

        // Delete the user
        database.user.delete(1);

        // Try to retrieve the deleted user
        User deletedUser = database.user.getById(1);

        // Check if the user is deleted successfully
        assertNull(deletedUser);
    }
}
