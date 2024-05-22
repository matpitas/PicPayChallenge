package tech.buildrun.picpay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_wallet_type")
public class WalletType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    public WalletType() {
    }

    public WalletType(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public void setId (Long id) {
        this.id = id;
    }
    
    public Long getId () {
        return this.id;
    }
    public String getDescription () {
        return this.description;
    }
    
    public void setDescription (String description) {
        this.description = description;
    }

    public enum Enum {

        USER(1L, "user"),
        MERCHANT(2L, "merchant");

        private Enum(Long id, String description) {
            this.id = id;
            this.description = description;
        }

        private Long id;
        private String description;

        public WalletType get() {
            return new WalletType(id, description);
        }

    }

}
