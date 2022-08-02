package me.realimpact.dummy.swing;

import me.realimpact.dummy.swing.domain.*;
import me.realimpact.dummy.swing.domain.MobilePhoneService.MobilePhoneServiceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

public class Fixtures {
  public static List<Customer> createManyCustomers() {
    return List.of(
            Customer.builder().ci("11111111111111111111").custNm("강인수").birthDt(LocalDate.of(1982,1,1)).build(),
            Customer.builder().ci("22222222222222222222").custNm("김도희").birthDt(LocalDate.of(1982,1,1)).build(),
            Customer.builder().ci("33333333333333333333").custNm("김범수").birthDt(LocalDate.of(1982,1,1)).build(),
            Customer.builder().ci("44444444444444444444").custNm("김선혁").birthDt(LocalDate.of(1982,1,1)).build(),
            Customer.builder().ci("55555555555555555555").custNm("이수경").birthDt(LocalDate.of(1982,1,1)).build(),
            Customer.builder().ci("66666666666666666666").custNm("황치훈").birthDt(LocalDate.of(1982,1,1)).build()
    );
  }
  
  public static List<Product> createManyProducts() {
    return List.of(
            Product.builder().prodId("NA00000001").prodNm("스페셜").isMobilePhoneLinkedDiscountTarget(true).build(),
            Product.builder().prodId("NA00000002").prodNm("플래티넘").isMobilePhoneLinkedDiscountTarget(true).build(),
            Product.builder().prodId("NA00000003").prodNm("스몰").isMobilePhoneLinkedDiscountTarget(false).build(),
            Product.builder().prodId("NA00000004").prodNm("미니").isMobilePhoneLinkedDiscountTarget(false).build()
    );
  }
  
  public static List<MobilePhoneService> createManyServices(List<Customer> customers, List<Product> products) {
    return List.of(
            MobilePhoneService.subscribe(customers.get(0), "1", LocalDate.of(2000,1,1), products.get(0)),
            MobilePhoneService.subscribe(customers.get(0), "2", LocalDate.of(2001,1,1), products.get(1)),
            MobilePhoneService.subscribe(customers.get(1), "3", LocalDate.of(2002,1,1), products.get(1)),
            MobilePhoneService.subscribe(customers.get(2), "4", LocalDate.of(2003,1,1), products.get(2)),
            MobilePhoneService.subscribe(customers.get(2), "5", LocalDate.of(2004,1,1), products.get(3)),
            MobilePhoneService.subscribe(customers.get(2), "6", LocalDate.of(2005,1,1), products.get(1)),
            MobilePhoneService.subscribe(customers.get(2), "7", LocalDate.of(2006,1,1), products.get(0))
    );
  }
}
