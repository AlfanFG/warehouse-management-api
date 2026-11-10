package alfan.dev.warehouse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record ItemDto(
        UUID id,
        @NotBlank(message = "Name is required") String name,
        String description,
        List<@Valid ItemVariantDto> variants
) {
}
