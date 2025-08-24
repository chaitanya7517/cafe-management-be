package my_spring_backend.demo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class OrderItem {
    private String itemCode;   // reference to MenuItem
    private String itemName;   // redundant copy for faster fetch
    private int quantity;
    private double price;
}
