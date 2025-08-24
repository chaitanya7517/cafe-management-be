package my_spring_backend.demo.controller;


import my_spring_backend.demo.model.Order;
import my_spring_backend.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(service.createOrder(order));
    }

    /* not much use of this api */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Order>> getOrdersByOwner(
            @PathVariable String ownerId,
            @RequestParam(defaultValue = "desc") String sortBy) {

        List<Order> orders = service.getOrdersByOwner(ownerId, sortBy);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<Order>> getOrdersByCustomerName(@PathVariable String customerName) {
        return ResponseEntity.ok(service.getOrdersByCustomerName(customerName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @RequestBody Order order) {
        return service.updateOrder(id, order)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable String id) {
        return service.deleteOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

