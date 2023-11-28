package database;

import Entities.Company;
import Entities.User;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.postgresql.core.Encoding;
import org.postgresql.util.HStoreConverter;

public class Database {
    String jdbcUrl = "jdbc:postgresql://localhost:5432/onlinetrade";
    String username = "postgres";
    String password = "mypassword";

    public Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            System.out.println("something wrong with database username and or" +
                    " password and or url");
        }
    }

    private String Hstoryfy(Map<Long, Integer> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (var i : map.entrySet()) {
            stringBuilder.append(String.format("'\"%s\"=>\"%s\"',",
                    i.getKey().toString()
                    , i.getValue().toString()));
        }
        return stringBuilder.toString();
    }

    public Database(Connection connection) {
        this.connection = connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void dropConnection() {
        try {
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class InMemoryUser {
        public User getById(long id) {

            try {
                String selectUserByIdQuery = "SELECT id, name, money, shares " +
                        "FROM users WHERE id = ?";
                PreparedStatement preparedStatement =
                        connection.prepareStatement(selectUserByIdQuery);
                // Установка значения параметра (замените user_id на конкретный идентификатор пользователя)
                preparedStatement.setLong(1, id);

                // Выполнение запроса и получение результата
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int money = resultSet.getInt("money");

                    Map<String, String> shares;
                    try {
                        shares =
                                HStoreConverter.fromBytes(resultSet.getBytes("shares"), Encoding.defaultEncoding());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        shares = new HashMap<>();
                    }
                    Map<Long, Integer> sharesConverted = new HashMap<>();
                    for (var keys : shares.entrySet()) {
                        sharesConverted.put(Long.getLong(keys.getKey()),
                                Integer.valueOf(keys.getValue()));
                    }
                    return new User(id, name, money, sharesConverted);
                } else {
                    System.out.println("User not found.");
                    return null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public void create(User user) {

            // SQL-запрос для добавления пользователя
            String insertUserQuery = "INSERT INTO users (id, name, money, shares) VALUES (?, ?, ?, ?)";

            // Создание PreparedStatement для выполнения запроса
            try {
                PreparedStatement preparedStatement =
                        connection.prepareStatement(insertUserQuery);
                // Установка значений параметров
                preparedStatement.setString(4,
                        Hstoryfy(user.getShares()));
                preparedStatement =
                        connection.prepareStatement(preparedStatement.toString());
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setString(2, user.getUserName());
                preparedStatement.setDouble(3, user.getMoney());
                // Выполнение запроса
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User added successfully.");
                } else {
                    System.out.println("Failed to add user.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void update(User user) {
            // SQL-запрос для обновления пользователя
            String updateUserQuery = "UPDATE users SET name = ?, money = ?, shares = ? WHERE id = ?";

            // Создание PreparedStatement для выполнения запроса
            try {
                PreparedStatement preparedStatement =
                        connection.prepareStatement(updateUserQuery);
                // Установка значений параметров
                preparedStatement.setString(3, Hstoryfy(user.getShares()));
                preparedStatement =
                        connection.prepareStatement(preparedStatement.toString());
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setDouble(2, user.getMoney());
                preparedStatement.setLong(3, user.getId());

                // Выполнение запроса
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User updated successfully.");
                } else {
                    System.out.println("User not found or update failed.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void delete(long id) {
            String deleteUserQuery = "DELETE FROM users WHERE id = ?";

            try {
                // Создание PreparedStatement для выполнения запроса
                PreparedStatement preparedStatement =
                        connection.prepareStatement(deleteUserQuery);
                // Установка значения параметра
                preparedStatement.setLong(1, id);

                // Выполнение запроса
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User deleted successfully.");
                } else {
                    System.out.println("User not found or deletion failed.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public class InMemoryCompany {
        public List<Company> getAll() {
            List<Company> companies = new ArrayList<>();

            // SQL-запрос для получения всех компаний
            String getAllCompaniesQuery = "SELECT * FROM companies";
            try (PreparedStatement preparedStatement = connection.prepareStatement(getAllCompaniesQuery)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        String companyName = resultSet.getString("name");
                        int totalShares = resultSet.getInt("total_shares");
                        int vacantShares = resultSet.getInt("vacant_shares");
                        float keyShareholderThreshold = resultSet.getFloat("key_shareholder_threshold");
                        long money = resultSet.getLong("money");
                        long sharePrice = resultSet.getLong("share_price");

                        // Получение списка пользователей
                        Set<User> users = getUsersForCompany(connection, id);

                        Company company = new Company(id, companyName, totalShares, vacantShares,
                                keyShareholderThreshold, money, sharePrice, users);

                        companies.add(company);
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return companies;
        }

        private Set<User> getUsersForCompany(Connection connection, long companyId) throws SQLException {
            Set<User> users = new HashSet<>();

            // SQL-запрос для получения пользователей, связанных с компанией
            String getUsersForCompanyQuery = "SELECT companies.users from " +
                    "companies " +
                    " Where " +
                    "companies.id = ?";

            try {
                PreparedStatement preparedStatement =
                        connection.prepareStatement(getUsersForCompanyQuery);
                preparedStatement.setLong(1, companyId);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Set<Integer> users1;
                    try {
                        users1 = Set.of((Integer[]) resultSet.getArray(
                                "users").getArray());
                    } catch (NullPointerException e) {
                        users1 = new HashSet<>();
                    }
                    for (var i : users1) {
                        users.add(new InMemoryUser().getById(i));
                    }
                } else {
                    throw new IOException("no company (");
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
            return users;
        }

        public Company getById(long id) {
            Company company = null;

            // SQL-запрос для получения компании по ID
            String getCompanyByIdQuery = "SELECT * FROM companies WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(getCompanyByIdQuery)) {
                preparedStatement.setLong(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String companyName = resultSet.getString("name");
                        int totalShares = resultSet.getInt("total_shares");
                        int vacantShares = resultSet.getInt("vacant_shares");
                        int keyShareholderThreshold = resultSet.getInt(
                                "key_shareholder_threshold");
                        long money = resultSet.getLong("money");
                        long sharePrice = resultSet.getLong("share_price");

                        // Получение списка пользователей
                        Set<User> users = getUsersForCompany(connection, id);

                        company = new Company(id, companyName, totalShares, vacantShares,
                                keyShareholderThreshold, money, sharePrice, users);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return company;
        }

        public synchronized void create(Company company) {

            // SQL-запрос для создания компании
            String createCompanyQuery = "INSERT INTO companies (id, name, total_shares, vacant_shares, key_shareholder_threshold, money, share_price) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createCompanyQuery)) {
                preparedStatement.setLong(1, company.getId());
                preparedStatement.setString(2, company.getCompanyName());
                preparedStatement.setInt(3, company.getTotalShares());
                preparedStatement.setInt(4, company.getVacantShares());
                preparedStatement.setInt(5, company.getKeyShareholderThreshold());
                preparedStatement.setLong(6, company.getMoney());
                preparedStatement.setLong(7, company.getSharePrice());

                // Выполнение запроса
                preparedStatement.executeUpdate();
                if (!company.getUsers().isEmpty()) {
                    throw new IOException("ты чо, продажа акций вне платформы" +
                            " запрещена");
                }

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void update(Company company) {

            // SQL-запрос для обновления компании
            String updateCompanyQuery = "UPDATE companies SET name = ?, total_shares = ?, vacant_shares = ?, key_shareholder_threshold = ?, money = ?, share_price = ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateCompanyQuery)) {
                preparedStatement.setString(1, company.getCompanyName());
                preparedStatement.setInt(2, company.getTotalShares());
                preparedStatement.setInt(3, company.getVacantShares());
                preparedStatement.setFloat(4, company.getKeyShareholderThreshold());
                preparedStatement.setLong(5, company.getMoney());
                preparedStatement.setLong(6, company.getSharePrice());
                preparedStatement.setLong(7, company.getId());

                // Выполнение запроса
                preparedStatement.executeUpdate();

                // Обновление связей с пользователями
                updateUsersForCompany(connection, company.getId(), company.getUsers());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Вспомогательный метод для обновления связей компании с пользователями
        private void updateUsersForCompany(Connection connection,
                                           long companyId, Set<User> users) throws SQLException {//todo
            // SQL-запрос для удаления текущих связей компании с пользователями
            String deleteUsersForCompanyQuery = "DELETE users FROM companies " +
                    "WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUsersForCompanyQuery)) {
                preparedStatement.setLong(1, companyId);

                // Удаление текущих связей
                preparedStatement.executeUpdate();
            }

            // Добавление новых связей
            addUsersToCompany(companyId, users);
        }

        // Наличие этой функции показывает огромный уровень доверия в нашей
        // команде
        public void addUsersToCompany(long companyId, Set<User> users) {
            try {
                // SQL-запрос для получения компании по ID
                String getCompanyByIdQuery = "SELECT users FROM companies WHERE " +
                        "id = ?";
                PreparedStatement preparedStatement =
                        connection.prepareStatement(getCompanyByIdQuery);
                preparedStatement.setLong(1, companyId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Set<Integer> usersAlreadyIn;
                    try {
                        usersAlreadyIn =
                                Set.of((Integer[]) resultSet.getArray(
                                        "users").getArray());
                    } catch (NullPointerException e) {
                        usersAlreadyIn = new HashSet<>();
                    }
                    for (var i : users) {
                        usersAlreadyIn.add((int) i.getId());
                    }
                    // SQL-запрос для обновления компании
                    String updateCompanyQuery = "UPDATE companies SET users = ? WHERE" +
                            " id = ?";
                    preparedStatement =
                            connection.prepareStatement(updateCompanyQuery);
                    preparedStatement.setLong(2, companyId);
                    preparedStatement.setArray(1, connection.createArrayOf(
                            "integer", usersAlreadyIn.toArray()));
                    preparedStatement.execute();
                } else {
                    throw new IOException("no company id");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }

        public void delete(long id) {
            // SQL-запрос для удаления компании по ID
            String deleteCompanyQuery = "DELETE FROM companies WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteCompanyQuery)) {
                preparedStatement.setLong(1, id);

                // Выполнение запроса
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Company deleted successfully.");
                } else {
                    System.out.println("Company not found or deletion failed.");

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }


}
