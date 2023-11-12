package Responses;

import DTOs.UserDTO;

import java.util.List;

public record FindCompanyResponse (
        String companyName, int totalShares, int vacantShares, float keyShareholderThreshold,
        long money, long sharePrice, List<UserDTO> users) {
}
