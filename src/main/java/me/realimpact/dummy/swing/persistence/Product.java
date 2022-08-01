package me.realimpact.dummy.swing.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.realimpact.dummy.swing.persistence.converter.BooleanToYNConverter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
  @Id
  @Column(name = "prod_id")
  private String id;

  @Version
  private Integer version;

  @Column(nullable = false)
  private String prodNm;

  @Convert(converter = BooleanToYNConverter.class)
  private boolean isMobilePhoneLinkedDiscountTarget;

  public Product(
      String prodId,
      String prodNm,
      boolean isMobilePhoneLinkedDiscountTarget
  ) {
    this.id = prodId;
    this.prodNm = prodNm;
    this.isMobilePhoneLinkedDiscountTarget = isMobilePhoneLinkedDiscountTarget;
  }
}
