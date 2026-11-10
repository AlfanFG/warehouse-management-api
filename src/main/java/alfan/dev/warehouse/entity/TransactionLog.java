package alfan.dev.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_logs")
public class TransactionLog extends BaseEntity {
    @Id
    @Column(name = "idempotency_key", updatable = false, nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status;
}
