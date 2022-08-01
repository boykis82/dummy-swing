package me.realimpact.dummy.swing.web;

import me.realimpact.dummy.swing.dto.ReqRelSvcAndOlmagoCustDto;
import me.realimpact.dummy.swing.dto.MobilePhoneResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/swing/api/v1/mobilephones")
public class OlmagoController {

  @GetMapping
  public List<MobilePhoneResponseDto> getServicesByCI(@RequestParam("ci") String ci) {
    return null;
  }

  @PostMapping("/{svc-mgmt-num}/linked-olmago-customer")
  public void linkOlmagoCustomerWithMobilePhoneService(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ReqRelSvcAndOlmagoCustDto requestOlmagoCustomerRelationDto
  ) {

  }

  @DeleteMapping("/{svc-mgmt-num}/linked-olmago-customer")
  public void unlinkOlmagoCustomerWithMobilePhoneService(
      @PathVariable("svc-mgmt-num") long svcMgmtNum,
      @RequestBody ReqRelSvcAndOlmagoCustDto requestOlmagoCustomerRelationDto
  ) {

  }
}
