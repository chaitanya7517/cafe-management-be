package my_spring_backend.demo.repository;

import my_spring_backend.demo.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerNameRegex(String regex);
    List<Order> findByOwnerId(String ownerId, Sort sort);
}
