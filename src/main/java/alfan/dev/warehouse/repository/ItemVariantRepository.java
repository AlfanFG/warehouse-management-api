package alfan.dev.warehouse.repository;

import alfan.dev.warehouse.entity.ItemVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ItemVariantRepository extends JpaRepository<ItemVariant, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT iv FROM ItemVariant iv WHERE iv.id = :id")
    Optional<ItemVariant> findByIdWithLock(@Param("id") UUID id);
}
