package alfan.dev.warehouse.service;

import alfan.dev.warehouse.dto.ItemDto;
import alfan.dev.warehouse.dto.ItemVariantDto;
import alfan.dev.warehouse.dto.SellRequest;
import alfan.dev.warehouse.dto.SellResponse;
import alfan.dev.warehouse.entity.Item;
import alfan.dev.warehouse.entity.ItemVariant;
import alfan.dev.warehouse.entity.TransactionLog;
import alfan.dev.warehouse.exception.OutOfStockException;
import alfan.dev.warehouse.exception.ResourceNotFoundException;
import alfan.dev.warehouse.mapper.InventoryMapper;
import alfan.dev.warehouse.repository.ItemRepository;
import alfan.dev.warehouse.repository.ItemVariantRepository;


import alfan.dev.warehouse.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemRepository itemRepository;

    private final ItemVariantRepository itemVariantRepository;

    private final TransactionLogRepository transactionLogRepository;

    private final InventoryMapper mapper;

    @Transactional
    public ItemDto createItem(ItemDto request) {
        Item item = mapper.toEntity(request);

        if (request.variants() != null) {
            request.variants().forEach(vDto -> {
                ItemVariant variant = mapper.toEntity(vDto);
                variant.setSku(resolveSku(vDto.sku(), request.name(), vDto.variantName()));
                item.addVariant(variant);
            });
        }

        Item savedItem = itemRepository.save(item);
        return mapper.toDto(savedItem);
    }

    @Transactional(readOnly = true)
    public Page<ItemDto> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public ItemDto getItemById(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        return mapper.toDto(item);
    }

    @Transactional
    public ItemDto updateItem(UUID id, ItemDto request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        item.setName(request.name());
        item.setDescription(request.description());

        return mapper.toDto(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        itemRepository.delete(item);
    }

    @Transactional
    public ItemVariantDto addVariant(UUID itemId, ItemVariantDto request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        ItemVariant variant = mapper.toEntity(request);
        variant.setSku(resolveSku(request.sku(), item.getName(), request.variantName()));
        item.addVariant(variant);
        ItemVariant savedVariant = itemVariantRepository.save(variant);

        return mapper.toDto(savedVariant);
    }

    @Transactional
    public ItemVariantDto updateVariant(UUID variantId, ItemVariantDto request) {
        ItemVariant variant = itemVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        variant.setVariantName(request.variantName());
        variant.setPrice(request.price());
        variant.setStockQuantity(request.stockQuantity());

        // Update SKU jika disediakan, jika tidak biarkan SKU lama
        if (request.sku() != null && !request.sku().isBlank()) {
            variant.setSku(request.sku());
        }

        return mapper.toDto(itemVariantRepository.save(variant));
    }

    @Transactional
    public void deleteVariant(UUID variantId) {
        ItemVariant variant = itemVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
        itemVariantRepository.delete(variant);
    }

    // --- SALES / TRANSACTION OPERATION (PRO LEVEL) ---

    @Transactional
    public SellResponse processSale(String idempotencyKey, SellRequest request) {
        // 1. Idempotency Check: Cegah double processing karena network retry
        System.out.println("HALLO: " + idempotencyKey);
        if (transactionLogRepository.existsById(idempotencyKey)) {
            return new SellResponse("IDEMPOTENT_SUCCESS", "Transaction already processed", request.variantId(), null);
        }

        // 2. Pessimistic Write Lock: Kunci row di DB agar request concurrent antre
        ItemVariant variant = itemVariantRepository.findByIdWithLock(request.variantId()).orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        // 3. Out-of-Stock Guard
        if (variant.getStockQuantity() < request.quantity()) {
            throw new OutOfStockException(
                    String.format("Insufficient stock for SKU: %s. Requested: %d, Available: %d",
                            variant.getSku(), request.quantity(), variant.getStockQuantity())
            );
        }

        // 4. Potong Stok
        int newStock = variant.getStockQuantity() - request.quantity();
        variant.setStockQuantity(newStock);
        itemVariantRepository.save(variant);

        // 5. Catat log transaksi agar Idempotency Key tersimpan
        TransactionLog log = new TransactionLog();
        log.setIdempotencyKey(idempotencyKey);
        log.setVariantId(request.variantId());
        log.setQuantity(request.quantity());
        log.setStatus("SUCCESS");
        transactionLogRepository.save(log);

        return new SellResponse("SUCCESS", "Sale processed successfully", variant.getId(), newStock);
    }

    private String resolveSku(String inputSku, String itemName, String variantName) {
        if (inputSku != null && !inputSku.isBlank()) {
            return inputSku.trim().toUpperCase();
        }
        //itemName dapat diganti jadi itemCode jika membuat code
        String prefix = (itemName + "-" + variantName)
                .replaceAll("[^a-zA-Z0-9]", "-")
                .toUpperCase();
        String shortHash = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + "-" + shortHash;
    }
}

