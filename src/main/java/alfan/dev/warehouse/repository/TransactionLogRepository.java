package alfan.dev.warehouse.repository;

import alfan.dev.warehouse.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, String> {
}
