package me.realimpact.dummy.swing.web;

import me.realimpact.dummy.swing.dto.ReqRelMobilePhoneAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.service.OlmagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swing/api/v1/mobile-phones")
public class OlmagoController {
  private final OlmagoService olmagoService;
  
  @Autowired
  public OlmagoController(OlmagoService olmagoService) {
    this.olmagoService = olmagoService;
  }

  @PostMapping("/{svc-mgmt-num}/linked-olmago-customer")
  public ResponseEntity<MobilePhoneAndOlmagoRelationResponseDto> linkOlmagoCustomerWithMobilePhoneService(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ReqRelMobilePhoneAndOlmagoCustDto reqRelMobilePhoneAndOlmagoCustDto
  ) {
    return ResponseEntity.ok()
        .body(olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqRelMobilePhoneAndOlmagoCustDto));
  }

  @PutMapping("/{svc-mgmt-num}/linked-olmago-customer/{olmago-customer-id}")
  public ResponseEntity<MobilePhoneAndOlmagoRelationResponseDto> unlinkOlmagoCustomerWithMobilePhoneService(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @PathVariable("olmago-customer-id") long olmagoCustomerId,
      @RequestBody ReqRelMobilePhoneAndOlmagoCustDto reqRelMobilePhoneAndOlmagoCustDto
  ) {
    return ResponseEntity.ok()
        .body(olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqRelMobilePhoneAndOlmagoCustDto));
  }
}
