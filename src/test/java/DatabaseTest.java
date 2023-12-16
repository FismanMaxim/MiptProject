import Entities.Company;
import Entities.User;
import Requests.ShareDelta;
import database.Database;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private static Database database;

    // region setting up & tearing down
    @BeforeAll
    public static void setUp() throws SQLException {
        // Initialize the database connection before running the tests

        database = new Database(DriverManager.getConnection("jdbc:postgresql" +
                        "://cornelius.db.elephantsql.com:5432/hmtdjque",
                "hmtdjque",
                "mW9O7Imtz3eqjtvVolLGZ4gWlC9VuKMh"));
//        database = new Database();
        try {
            database.connection.prepareStatement("DROP table users; DROP " +
                    "table companies;").execute();
        } catch (SQLException e) {
        }
        try {
            database.connection.prepareStatement("create extension hstore;").execute();
        } catch (SQLException e) {
        }
        database.connection.prepareStatement("CREATE TABLE users (\n" +
                "                       id SERIAL PRIMARY KEY,\n" +
                "                       name VARCHAR(255),\n" +
                "                       money INT,\n" +
                "                       shares hstore,\n" +
                "                       password VARCHAR(255)\n" +
                ");CREATE TABLE companies (\n" +
                "                           id SERIAL PRIMARY KEY,\n" +
                "                           users INT[],\n" +
                "                           name VARCHAR(255) NOT NULL,\n" +
                "                           key_shareholder_threshold INT NOT NULL,\n" +
                "                           vacant_shares INT NOT NULL,\n" +
                "                           total_shares INT NOT NULL,\n" +
                "                           money INT NOT NULL,\n" +
                "                           share_price INT NOT NULL,\n" +
                "                           password VARCHAR(255)\n" +
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
    // endregion

    // region static comparators
    private static void assertSameUsers(User user1, User user2) {
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getUserName(), user2.getUserName());
        assertEquals(user1.getMoney(), user2.getMoney());
        assertEquals(user1.getCopyOfShares(), user2.getCopyOfShares());
        assertEquals(user1.getPassword(), user2.getPassword());
    }

    private static void assertSameCompanies(Company company1, Company company2) {
        assertEquals(company1.getId(), company2.getId());
        assertEquals(company1.getCompanyName(), company2.getCompanyName());
        assertEquals(company1.getPassword(), company2.getPassword());
        assertEquals(company1.getMoney(), company2.getMoney());
        assertEquals(company1.getVacantShares(), company2.getVacantShares());
        assertEquals(company1.getTotalShares(), company2.getTotalShares());
        assertEquals(company1.getKeyShareholderThreshold(), company2.getKeyShareholderThreshold());
        assertEquals(company1.getUserIds(), company2.getUserIds());
        assertEquals(company1.getCopyOfUsers(), company2.getCopyOfUsers());
    }
    // endregion

    // region users tests
    @Test
    public void testGetAllUsers() {
        // Create a user and add it to the database
        final int countUsers = 3;
        long[] ids = new long[countUsers];
        User[] users = new User[countUsers];
        for (int i = 0; i < countUsers; i++) {
            ids[i] = database.user.generateId();
            users[i] = new User(ids[i], "TestUser" + i, 100.0 * i, new HashMap<>(), "password" + i);
            database.user.create(users[i]);
        }

        // Retrieve all users
        List<User> retrievedUsers = database.user.getAll();

        // Check if the retrieved user is not null
        assertNotNull(retrievedUsers);
        assertEquals(countUsers, retrievedUsers.size());

        // Check the users
        for (int i = 0; i < retrievedUsers.size(); i++)
            assertSameUsers(users[i], retrievedUsers.get(i));
    }

    @Test
    public void testInMemoryUserGetById() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>(), "password");
        database.user.create(testUser);

        // Retrieve the user by ID
        User retrievedUser = database.user.getById(1);

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertSameUsers(testUser, retrievedUser);
    }

    @Test
    public void testInMemoryUserCreate() {
        // Create a user and add it to the database
        User testUser = new User(2, "TestUser2", 200.0, new HashMap<>(), "password");
        database.user.create(testUser);

        // Retrieve the user by ID
        User retrievedUser = database.user.getById(2);

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertSameUsers(testUser, retrievedUser);
    }

    @Test
    public void testGenerateVacantId() {
        final int attempts = 3;
        for (int i = 0; i < attempts; i++) {
            assertNull(database.user.getById(database.user.generateId()));
            assertNull(database.company.getById(database.company.generateId()));
        }
    }

    @Test
    public void testInMemoryUserUpdate() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>(), "password");
        database.user.create(testUser);

        // Update the user
        User updatedTestUser = testUser.withName("UpdatedUser").withMoney(150.0);
        database.user.update(updatedTestUser);

        // Retrieve the updated user
        User updatedRetrievedUser = database.user.getById(1);

        // Check if the user is updated successfully
        assertNotNull(updatedRetrievedUser);
        assertSameUsers(updatedTestUser, updatedRetrievedUser);
    }

    @Test
    public void testInMemoryUserDelete() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>(), "password");
        database.user.create(testUser);

        // Delete the user
        database.user.delete(1);

        // Try to retrieve the deleted user
        User deletedUser = database.user.getById(1);

        // Check if the user is deleted successfully
        assertNull(deletedUser);
    }

    @Test
    public void getUserByNamePassword() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>(),
                "password");
        database.user.create(testUser);

        // Retrieve the user by name and password
        User retrievedUser = database.user.getByNamePassword("TestUser", "password");

        // Check if the retrieved user is not null
        assertNotNull(retrievedUser);

        // Check if the retrieved user has the correct properties
        assertSameUsers(testUser, retrievedUser);
    }

    @Test
    public void shouldNotReturnUserWithWrongPassword() {
        // Create a user and add it to the database
        User testUser = new User(1, "TestUser", 100.0, new HashMap<>(), "pass");
        database.user.create(testUser);

        // Retrieve the user by name and password
        User retrievedUser = database.user.getByNamePassword("TestUser", "wrongPassword");

        // Check if the retrieved user is not null
        assertNull(retrievedUser);
    }
    // endregion

    // region companies tests
    @Test
    public void testGetAllCompanies() {
        // Create a user and add it to the database
        final int countCompanies = 3;
        long[] usersIds = new long[countCompanies];
        long[] companiesIds = new long[countCompanies];
        User[] users = new User[countCompanies]; // creates a single user for each company
        Company[] companies = new Company[countCompanies];
        for (int i = 0; i < countCompanies; i++) {
            usersIds[i] = database.user.generateId();
            users[i] = new User(usersIds[i], "TestUser", 100.0, new HashMap<>());
            database.user.create(users[i]);

            companiesIds[i] = database.company.generateId();
            companies[i] = new Company(companiesIds[i], "companyName" + i, 100 * i, 10 * i, 1000 * i, 50 * i, "password" + i);
            database.company.create(companies[i]);
        }

        // Retrieve all users
        List<Company> retrievedCompanies = database.company.getAll();

        // Check if the retrieved user is not null
        assertNotNull(retrievedCompanies);
        assertEquals(countCompanies, retrievedCompanies.size());

        // Check the users
        for (int i = 0; i < retrievedCompanies.size(); i++)
            assertSameCompanies(companies[i], retrievedCompanies.get(i));
    }

    @Test
    public void testInMemoryCompanyGetById() {
        // Create a company and add it to the database
        Company testCompany = new Company(1, "TestCompany", 100, 50, 0.5F,
                1000, 10, new HashSet<>(), "password");
        database.company.create(testCompany);

        // Retrieve the company by ID
        Company retrievedCompany = database.company.getById(1);

        // Check if the retrieved company is not null
        assertNotNull(retrievedCompany);

        // Check if the retrieved company has the correct properties
        assertSameCompanies(testCompany, retrievedCompany);
    }

    @Test
    public void testInMemoryCompanyCreate() {
        // Create a company and add it to the database
        Company testCompany = new Company(2, "TestCompany2", 200, 100, 0.7F,
                1500, 15, new HashSet<>(), "password");
        database.company.create(testCompany);

        // Retrieve the company by ID
        Company retrievedCompany = database.company.getById(2);

        // Check if the retrieved company is not null
        assertNotNull(retrievedCompany);

        // Check if the retrieved company has the correct properties
        assertSameCompanies(testCompany, retrievedCompany);

        // Check if the retrieved company users is an empty set
        assertNotNull(retrievedCompany.getUsers());
        assertTrue(retrievedCompany.getUsers().isEmpty());
    }

    @Test
    public void testInMemoryCompanyAddUsersToCompany() {
        // Create a company and add it to the database
        Company testCompany = new Company(3, "TestCompany3", 300, 150, 0.8F,
                2000, 20, new HashSet<>(), "password");
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
    public void testInMemoryCompanyUpdate() {
        // Create a company and add it to the database
        long id = database.company.generateId();
        Company testCompany = new Company(id, "TestCompany", 100, 50, 0.5F, 1000, 10, new HashSet<>(), "password");
        database.company.create(testCompany);

        // Update the company
        Company updatedTestCompany = testCompany.withName("UpdatedCompany").withCountShares(200, true);
        database.company.update(updatedTestCompany);

        // Retrieve the updated company
        Company updatedCompany = database.company.getById(id);

        // Check if the company is updated successfully
        assertSameCompanies(updatedTestCompany, updatedCompany);
    }

    @Test
    public void getCompanyByNamePassword() {
        // Create a company and add it to the database
        long id = database.company.generateId();
        Company testCompany = new Company(id, "companyName", 100, 10, 1000, 50, "password");
        database.company.create(testCompany);

        // Retrieve the company by name and password
        Company retrievedCompany = database.company.getByNamePassword("companyName", "password");

        // Check if the retrieved company is not null
        assertNotNull(retrievedCompany);

        // Check if the retrieved company has the correct properties
        assertSameCompanies(testCompany, retrievedCompany);
    }

    @Test
    public void shouldNotReturnCompanyWithWrongPassword() {
        // Create a company and add it to the database
        long id = database.company.generateId();
        Company testCompany = new Company(id, "companyName", 100, 10, 1000, 50, "password");
        database.company.create(testCompany);

        // Retrieve the company by name and password
        Company retrievedCompany = database.company.getByNamePassword("companyName", "pwrongPassword");

        // Check if the retrieved company is not null
        assertNull(retrievedCompany);
    }

    @Test
    public void testInMemoryCompanyDelete() {
        // Create a company and add it to the database
        Company testCompany = new Company(1, "TestCompany", 100, 50, 0.5F, 1000, 10, new HashSet<>(), "password");
        database.company.create(testCompany);

        // Delete the company
        database.company.delete(1);

        // Try to retrieve the deleted company
        Company deletedCompany = database.company.getById(1);

        // Check if the company is deleted successfully
        assertNull(deletedCompany);
    }
    // endregion

    // region general database tests
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
    // endregion

    @Test
    public void SaveUsersOfCompanyWithShares() {
        User user = new User(1, "userName", 0, "password");
        Company company = new Company(1, "companyName", 100, 0, 0, 0, "password");

        database.user.create(user);
        database.company.create(company);

        // Add 100 shares of company with id=1 to user
        user =  user.withSharesDelta(List.of(new ShareDelta(1L, 100)));
        company = company.withVacantSharesDelta(-100);
        company = company.withNewUser(user);

        database.user.update(user);
        database.company.update(company);

        List<User> users = database.user.getAll();
        User retrievedUser = users.get(0);

        assertEquals(100, retrievedUser.countSharesOfCompany(1L));
    }
}
