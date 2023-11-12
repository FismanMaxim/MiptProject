package DTOs;

import Entities.StoredById;

public interface EntityDTO<T extends StoredById> {
    T convertToTargetObject(long id);
}
