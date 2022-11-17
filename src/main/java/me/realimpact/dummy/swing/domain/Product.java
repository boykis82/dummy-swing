package me.realimpact.dummy.swing.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Version;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table(
    name = "zprd_prod"
)
public class Product extends BaseEntity {
  public enum ProductTier {
    PLATINUM,
    PRIME_PLUS,
    PRIME,
    MAX,
    SPECIAL,
    ZERO_PLAN,
    LOW
  }
  
  @Id
  @Column(name = "prod_id")
  private String prodId;

  @Version
  private Integer version;

  @Column(nullable = false, name = "prod_nm")
  private String prodNm;
  
  @Column(nullable = false, name = "product_tier")
  @Enumerated(value = EnumType.STRING)
  private ProductTier productTier;

  @Builder
  public Product(String prodId, String prodNm, ProductTier productTier) {
    this.prodId = prodId;
    this.prodNm = prodNm;
    this.productTier = productTier;
  }
}
