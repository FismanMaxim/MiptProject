package DTOs;

import Entities.User;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserDTOTest {
    @Test
    public void ConvertToTargetObject() {
        UserDTO dto = new UserDTO(1, "name", 10, Map.of(1L, 10, 2L, 20));

        User fromDTO = dto.convertToTargetObject("pass");

        assertEquals(fromDTO.getUserName(), "name");
        assertEquals(fromDTO.getMoney(), 10);
        assertEquals(fromDTO.getId(), 1);
        assertEquals(fromDTO.getPassword(), "pass");
    }

    @Test
    public void testEqualsById() {
        UserDTO dto1 = new UserDTO(1, "name1", 10, null);
        UserDTO dto2 = new UserDTO(1, "name2", 20, null);
        UserDTO dto3 = new UserDTO(2, "name1", 1, null);

        // DTOs must be compared ONLY by id
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }
}