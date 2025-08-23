package my_spring_backend.demo.service;

import my_spring_backend.demo.model.MenuItem;
import my_spring_backend.demo.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository repository;

    public MenuItemService(MenuItemRepository repository) {
        this.repository = repository;
    }

    public MenuItem addMenuItem(String ownerId, MenuItem item) {
        // ✅ Validation: itemCode must not be null or empty
        if (item.getItemCode() == null || item.getItemCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Item code is required");
        }

        // ✅ Check if item with same ownerId + itemCode already exists
        repository.findByOwnerIdAndItemCode(ownerId, item.getItemCode())
                .ifPresent(existingItem -> {
                    throw new RuntimeException("Item with code " + item.getItemCode() + " already exists for this owner");
                });

        // ✅ Set ownerId explicitly
        item.setOwnerId(ownerId);

        // ✅ Save new item
        return repository.save(item);
    }

    public List<MenuItem> getAllItemsByOwner(String ownerId) {
        return repository.findByOwnerId(ownerId);
    }

    public Optional<MenuItem> getItemByCode(String ownerId, String itemCode) {
        return repository.findByOwnerIdAndItemCode(ownerId, itemCode);
    }

    public MenuItem updateItem(String ownerId, String oldItemCode, MenuItem updatedItem) {
        // Find existing item by ownerId + old itemCode
        MenuItem existingItem = repository.findByOwnerIdAndItemCode(ownerId, oldItemCode)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Check if user is trying to update the itemCode
        if (!oldItemCode.equals(updatedItem.getItemCode())) {
            // If itemCode is changing, check if the new itemCode already exists for this owner
            boolean exists = repository.findByOwnerIdAndItemCode(ownerId, updatedItem.getItemCode()).isPresent();
            if (exists) {
                throw new RuntimeException("Item with code " + updatedItem.getItemCode() + " already exists for this user.");
            }
        }

        // Update details
        existingItem.setItemCode(updatedItem.getItemCode());
        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setImageUrl(updatedItem.getImageUrl());

        return repository.save(existingItem);
    }

    public void deleteItem(String ownerId, String itemCode) {
        repository.deleteByOwnerIdAndItemCode(ownerId, itemCode);
    }

}

