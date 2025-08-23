package my_spring_backend.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "menu_items")
public class MenuItem {

    @Id
    private String id;  // MongoDB generated ObjectId

    private String ownerId;  // To identify which owner the item belongs to
    private String itemCode; // Short custom code per owner (primary for business logic)
    private String name;
    private String description;
    private Double price;
    private String imageUrl; // optional
    private boolean available = true; // to toggle availability
}