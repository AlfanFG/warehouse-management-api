package alfan.dev.warehouse.service;

import alfan.dev.warehouse.dto.ItemDto;
import alfan.dev.warehouse.dto.ItemVariantDto;
import alfan.dev.warehouse.entity.Item;
import alfan.dev.warehouse.entity.ItemVariant;
import alfan.dev.warehouse.exception.ResourceNotFoundException;
import alfan.dev.warehouse.mapper.InventoryMapper;
import alfan.dev.warehouse.repository.ItemRepository;
import alfan.dev.warehouse.repository.ItemVariantRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private InventoryMapper mapper;

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
