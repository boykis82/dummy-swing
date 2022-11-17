package me.realimpact.dummy.swing.web;

import lombok.RequiredArgsConstructor;
import me.realimpact.dummy.swing.dto.*;
import me.realimpact.dummy.swing.service.MobilePhoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/swing/api/v1/mobile-phones")
public class MobilePhoneController {
  private final MobilePhoneService mobilePhoneService;

  @GetMapping
  public ResponseEntity<List<MobilePhoneResponseDto>> getMobilePhonesByCI(@RequestParam("ci") String ci) {
    return ResponseEntity.ok().body(mobilePhoneService.getMobilePhonesByCi(ci));
  }

  @PutMapping("/{svc-mgmt-num}/owner-customer")
  public ResponseEntity<Void> changeOwner(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ChangeOwnerRequestDto reqDto
  ) {
    mobilePhoneService.changeOwner(reqDto);

    return ResponseEntity.ok().build();
  }
  
  @DeleteMapping("/{svc-mgmt-num}")
  public ResponseEntity<Void> terminate(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody TerminateRequestDto reqDto
  ) {
    mobilePhoneService.terminate(reqDto);
    return ResponseEntity.ok().build();
  }
  
  @PutMapping("/{svc-mgmt-num}/product")
  public ResponseEntity<Void> changeFeeProduct(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ChangeFeeProductRequestDto reqDto
  ) {
    mobilePhoneService.changeFeeProduct(reqDto);
    return ResponseEntity.ok().build();
  }
}
