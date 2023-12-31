package Entities;

import CustomExceptions.NegativeSharesException;
import Requests.ShareDelta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {
    @Test
    void calculateKeyShareholders() throws NegativeSharesException {
        // Assign
        Company company = new Company(0, "testName",
                100, 50, 0, 100, "password");
        User user1 = new User(1, "user1", 1000, "p1");
        User user2 = new User(2, "user2", 1000, "p2");

        user1 = user1.withSharesDelta(List.of(new ShareDelta(0L, 25)));
        user2 = user2.withSharesDelta(List.of(new ShareDelta(0L, 50)));

        company = company.withNewUser(user1).withNewUser(user2);

        // Act
        var keyShareholders = company.getKeyShareholders();

        // Assert
        assertEquals(List.of(user2), keyShareholders);
    }

    @Test
    void withMethods() {
        // Assign
        Company company = new Company(0, "testName", 100, 50, 1000, 100, "Password");

        // Act
        company = company
                .withName("newName")
                .withDeltaMoney(10)
                .withThreshold(10)
                .withVacantSharesDelta(10)
                .withCountShares(1000, false);

        assert company.getCompanyName().equals("newName");
        assert company.getMoney() == 1010;
        assert company.getKeyShareholderThreshold() == 10;
        assert company.getVacantShares() == 1000;
        assert company.getTotalShares() == 1000;
    }
}