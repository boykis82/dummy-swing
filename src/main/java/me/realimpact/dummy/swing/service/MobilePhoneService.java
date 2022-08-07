package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.dto.*;

import java.util.List;

public interface MobilePhoneService {
  List<MobilePhoneResponseDto> getMobilePhonesByCi(String ci);
  void changeOwner(ChangeOwnerRequestDto dto);
  void terminate(TerminateRequestDto dto);
  void changeFeeProduct(ChangeFeeProductRequestDto dto);
}
