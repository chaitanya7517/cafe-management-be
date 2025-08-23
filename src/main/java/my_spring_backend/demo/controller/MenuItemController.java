package my_spring_backend.demo.controller;

import my_spring_backend.demo.model.MenuItem;
import my_spring_backend.demo.service.MenuItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @PostMapping("/{ownerId}")
    public MenuItem addItem(@PathVariable String ownerId, @RequestBody MenuItem item) {
        return service.addMenuItem(ownerId,item);
    }

    @GetMapping("/{ownerId}")
    public List<MenuItem> getAllItems(@PathVariable String ownerId) {
        return service.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/{ownerId}/{itemCode}")
    public MenuItem getItem(@PathVariable String ownerId, @PathVariable String itemCode) {
        return service.getItemByCode(ownerId, itemCode).orElseThrow();
    }

    @PutMapping("/{ownerId}/{itemCode}")
    public MenuItem updateItem(@PathVariable String ownerId, @PathVariable String itemCode, @RequestBody MenuItem item) {
        return service.updateItem(ownerId, itemCode, item);
    }


    @DeleteMapping("/{ownerId}/{itemCode}")
    public String deleteItem(@PathVariable String ownerId, @PathVariable String itemCode) {
        service.deleteItem(ownerId, itemCode);
        return "deleted!!";
    }

}
