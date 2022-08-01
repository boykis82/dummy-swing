package me.realimpact.dummy.swing.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.realimpact.dummy.swing.domain.converter.BooleanToYNConverter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.*;

@Entity
@Data
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

  public Product(String prodId, String prodNm, boolean isMobilePhoneLinkedDiscountTarget) {
    this.id = prodId;
    this.prodNm = prodNm;
    this.isMobilePhoneLinkedDiscountTarget = isMobilePhoneLinkedDiscountTarget;
  }
}
