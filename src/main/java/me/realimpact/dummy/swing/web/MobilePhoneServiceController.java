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
  MobilePhoneServiceService mobilePhoneServiceService;
  
  @Autowired
  public MobilePhoneServiceController(MobilePhoneServiceService mobilePhoneServiceService) {
    this.mobilePhoneServiceService = mobilePhoneServiceService;
  }

  @PutMapping("/{svc-mgmt-num}/owner-customer")
  public ResponseEntity<Void> changeOwner(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ChangeOwnerRequestDto reqDto
  ) {
    mobilePhoneServiceService.changeOwner(reqDto);
    return ResponseEntity.ok().build();
  }
  
  @DeleteMapping("/{svc-mgmt-num}")
  public ResponseEntity<Void> terminate(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody TerminateRequestDto reqDto
  ) {
    mobilePhoneServiceService.terminate(reqDto);
    return ResponseEntity.ok().build();
  }
  
  @PutMapping("/{svc-mgmt-num}/product")
  public ResponseEntity<Void> changeFeeProduct(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ChangeFeeProductRequestDto reqDto
  ) {
    mobilePhoneServiceService.changeFeeProduct(reqDto);
    return ResponseEntity.ok().build();
  }
}
