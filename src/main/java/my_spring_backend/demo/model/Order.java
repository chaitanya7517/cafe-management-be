package my_spring_backend.demo.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
@Data
public class Order {
    @Id
    private String id;

    private String customerName;
    private String customerMobileNo;
    private String ownerId;
    private List<OrderItem> items;

    private double totalAmount;
    private String status = "PENDING"; // PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELLED
    @CreatedDate
    private Instant createdAt;

    public double calculateTotalAmount() {
        if (items != null) {
            this.totalAmount = items.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        } else {
            this.totalAmount = 0.0;
        }
        return this.totalAmount;
    }
}