package database;

import Entities.User;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.postgresql.*;
import org.postgresql.core.Encoding;
import org.postgresql.util.HStoreConverter;

public class Database {
    // Замените эти значения вашими реальными данными
    String jdbcUrl = "jdbc:postgresql://localhost:5432/onlinetrade";
    String username = "postgres";
    String password = "mypassword";
    Connection connection;

    Database() {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            System.out.println("something wrong with database username and or" +
                    " password and or url");
        }
    }

    User getById(long id) {

        String selectUserByIdQuery = "SELECT id, name, money FROM users WHERE id = ?";
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(selectUserByIdQuery);
            // Установка значения параметра (замените user_id на конкретный идентификатор пользователя)
            preparedStatement.setLong(1, id);

            // Выполнение запроса и получение результата
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int money = resultSet.getInt("money");
                var shares =
                        HStoreConverter.fromBytes(resultSet.getBytes("shares"), Encoding.defaultEncoding());
                Map<Long, Integer> sharesConverted = new HashMap<>();
                for (var keys : shares.entrySet()) {
                    sharesConverted.put(Long.getLong(keys.getKey()),
                            Integer.valueOf(keys.getValue()));
                }
                return new User(id, name, money, sharesConverted);
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
