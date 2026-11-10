package alfan.dev.warehouse.controller;

import alfan.dev.warehouse.dto.*;
import alfan.dev.warehouse.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<ItemDto>> createItem(@Valid @RequestBody ItemDto request) {
        ItemDto createdItem = inventoryService.createItem(request);

        return ResponseEntity.ok(ApiResponse.success("Item created successfully", createdItem));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<Page<ItemDto>>> getItems(Pageable pageable) {
        Page<ItemDto> items = inventoryService.getAllItems(pageable);
        return ResponseEntity.ok(ApiResponse.success("Items retrieved successfully", items));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> getItemById(@PathVariable UUID id) {
        ItemDto data = inventoryService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Item fetched successfully", data));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> updateItem(@PathVariable UUID id, @Valid @RequestBody ItemDto request) {
        ItemDto data = inventoryService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", data));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable UUID id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully", null));
    }

    // --- VARIANT ENDPOINTS ---

    @PostMapping("/items/{itemId}/variants")
    public ResponseEntity<ApiResponse<ItemVariantDto>> addVariant(
            @PathVariable UUID itemId,
            @Valid @RequestBody ItemVariantDto request) {
        ItemVariantDto data = inventoryService.addVariant(itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Variant added successfully", data));
    }

    @PutMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<ItemVariantDto>> updateVariant(
            @PathVariable UUID variantId,
            @Valid @RequestBody ItemVariantDto request) {
        ItemVariantDto data = inventoryService.updateVariant(variantId, request);
        return ResponseEntity.ok(ApiResponse.success("Variant updated successfully", data));
    }

    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable UUID variantId) {
        inventoryService.deleteVariant(variantId);
        return ResponseEntity.ok(ApiResponse.success("Variant deleted successfully", null));
    }

    // --- TRANSACTION ENDPOINT ---

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<SellResponse>> sellItem(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody SellRequest request) {
        SellResponse data = inventoryService.processSale(idempotencyKey, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction completed", data));
    }
}
