package alfan.dev.warehouse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemVariantDto(
        UUID id,
        String sku,
        @NotBlank(message = "Variant name is required") String variantName,
        @NotNull(message = "Price is required") @PositiveOrZero BigDecimal price,
        @NotNull(message = "Stock is required") @Min(0) Integer stockQuantity
) {

}
