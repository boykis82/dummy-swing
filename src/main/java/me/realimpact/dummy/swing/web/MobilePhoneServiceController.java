package me.realimpact.dummy.swing.web;

import me.realimpact.dummy.swing.dto.*;
import me.realimpact.dummy.swing.service.MobilePhoneServiceService;
import me.realimpact.dummy.swing.service.OlmagoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swing/api/v1/mobile-phones")
public class MobilePhoneServiceController {
  @Autowired
  MobilePhoneServiceService mobilePhoneServiceService;
  
  @Autowired
  OlmagoClient olmagoClient;
  
  @PutMapping("/{svc-mgmt-num}/owner-customer")
  public ResponseEntity<Void> changeOwner(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ChangeOwnerRequestDto reqDto
  ) {
    ChangeOwnerResponseDto resDto = mobilePhoneServiceService.changeOwner(reqDto);
    if (resDto.getBfOlmagoCustomerId() > 0L) {
      olmagoClient.unlinkMobilePhoneService(resDto.getBfOlmagoCustomerId(), resDto.getSvcMgmtNum());
    }
    return ResponseEntity.ok().build();
  }
  
  @DeleteMapping("/{svc-mgmt-num}")
  public ResponseEntity<Void> terminate(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody TerminateRequestDto reqDto
  ) {
    TerminateResponseDto resDto = mobilePhoneServiceService.terminate(reqDto);
    if (resDto.getOlmagoCustomerId() > 0L) {
      olmagoClient.unlinkMobilePhoneService(resDto.getOlmagoCustomerId(), resDto.getSvcMgmtNum());
    }
    return ResponseEntity.ok().build();
  }
  
  @PutMapping("/{svc-mgmt-num}/product")
  public ResponseEntity<Void> changeFeeProduct(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ChangeFeeProductRequestDto reqDto
  ) {
    ChangeFeeProductResponseDto resDto = mobilePhoneServiceService.changeFeeProduct(reqDto);
    if (resDto.getMobilePhoneProductTierChangeType() == ChangeFeeProductResponseDto.ProductTierChangeType.UP) {
      olmagoClient.applyMobilePhoneLinkedDiscount(resDto.getOlmagoCustomerId(), resDto.getSvcMgmtNum());
    } else if (resDto.getMobilePhoneProductTierChangeType() == ChangeFeeProductResponseDto.ProductTierChangeType.DOWN) {
      olmagoClient.cancelMobilePhoneLinkedDiscount(resDto.getOlmagoCustomerId(), resDto.getSvcMgmtNum());
    }
    return ResponseEntity.ok().build();
  }
}
