package alfan.dev.warehouse.mapper;

import alfan.dev.warehouse.dto.ItemDto;
import alfan.dev.warehouse.dto.ItemVariantDto;
import alfan.dev.warehouse.entity.Item;
import alfan.dev.warehouse.entity.ItemVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

    @Mapping(target = "variants", ignore = true)
    Item toEntity(ItemDto itemDto);

    ItemVariant toEntity(ItemVariantDto itemVariantDto);

    ItemDto toDto(Item itemEntity);

    ItemVariantDto toDto(ItemVariant itemVariantEntity);

}
