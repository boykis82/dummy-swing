package me.realimpact.dummy.swing.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.realimpact.dummy.swing.domain.converter.BooleanToYNConverter;
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
  @Id
  @Column(name = "prod_id")
  private String prodId;

  @Version
  private Integer version;

  @Column(nullable = false, name = "prod_nm")
  private String prodNm;

  @Convert(converter = BooleanToYNConverter.class)
  @Column(name = "mbl_phone_linked_dc_yn")
  private boolean isMobilePhoneLinkedDiscountTarget;

  @Builder
  public Product(String prodId, String prodNm, boolean isMobilePhoneLinkedDiscountTarget) {
    this.prodId = prodId;
    this.prodNm = prodNm;
    this.isMobilePhoneLinkedDiscountTarget = isMobilePhoneLinkedDiscountTarget;
  }
}
