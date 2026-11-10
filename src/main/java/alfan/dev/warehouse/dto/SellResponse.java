package alfan.dev.warehouse.dto;

import java.util.UUID;

public record SellResponse(
        String transactionStatus,
        String message,
        UUID variantId,
        Integer remainingStock
) {

}
