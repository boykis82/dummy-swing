package me.realimpact.dummy.swing.web;

import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import me.realimpact.dummy.swing.dto.SvcAndOlmagoRelationResponseDto;
import me.realimpact.dummy.swing.service.OlmagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swing/api/v1/mobilephones")
public class OlmagoController {
  @Autowired
  private OlmagoService olmagoService;
  
  @GetMapping
  public ResponseEntity<List<MobilePhoneResponseDto>> getServicesByCI(@RequestParam("ci") String ci) {
    return ResponseEntity.ok().body(olmagoService.getServicesByCI(ci));
  }

  @PostMapping("/{svc-mgmt-num}/linked-olmago-customer")
  public ResponseEntity<SvcAndOlmagoRelationResponseDto> linkOlmagoCustomerWithMobilePhoneService(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto
  ) {
    return ResponseEntity.ok().body(olmagoService.linkOlmagoCustomerWithMobilePhoneService(reqRelSvcAndOlmagoCustDto));
  }

  @DeleteMapping("/{svc-mgmt-num}/linked-olmago-customer")
  public ResponseEntity<SvcAndOlmagoRelationResponseDto> unlinkOlmagoCustomerWithMobilePhoneService(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ReqRelSvcAndOlmagoCustDto reqRelSvcAndOlmagoCustDto
  ) {
    return ResponseEntity.ok().body(olmagoService.unlinkOlmagoCustomerWithMobilePhoneService(reqRelSvcAndOlmagoCustDto));
  }
}
