package my_spring_backend.demo.repository;

import my_spring_backend.demo.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {

    List<MenuItem> findByOwnerId(String ownerId);

    Optional<MenuItem> findByOwnerIdAndItemCode(String ownerId, String itemCode);

    void deleteByOwnerIdAndItemCode(String ownerId, String itemCode);
}

