package alfan.dev.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE items SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVariant> variants = new ArrayList<>();

    public void addVariant(ItemVariant variant) {
        variants.add(variant);
        variant.setItem(this);
    }

}
