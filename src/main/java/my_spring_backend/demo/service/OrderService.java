package my_spring_backend.demo.service;

import my_spring_backend.demo.model.Order;
import my_spring_backend.demo.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(Order order) {
        order.calculateTotalAmount();
        return repository.save(order);
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Order> getOrderById(String id) {
        return repository.findById(id);
    }

    public List<Order> getOrdersByOwner(String ownerId, String sortBy) {
        Sort sort = sortBy.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, "_id")
                : Sort.by(Sort.Direction.DESC, "_id");

        return repository.findByOwnerId(ownerId, sort);
    }

    public List<Order> getOrdersByCustomerName(String customerName) {
        return repository.findByCustomerNameRegex(customerName);
    }

    public Optional<Order> updateOrder(String id, Order updatedOrder) {
        return repository.findById(id).map(existing -> {
            existing.setCustomerName(updatedOrder.getCustomerName());
            existing.setCustomerMobileNo(updatedOrder.getCustomerMobileNo());
            existing.setItems(updatedOrder.getItems());
            existing.setTotalAmount(updatedOrder.calculateTotalAmount());
            existing.setStatus(updatedOrder.getStatus());
            return repository.save(existing);
        });
    }

    public Optional<Order> deleteOrder(String id) {
        Optional<Order> order = repository.findById(id);
        order.ifPresent(repository::delete);
        return order;
    }
}
