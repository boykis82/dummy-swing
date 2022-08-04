package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.dto.*;

public interface MobilePhoneServiceService {
  void changeOwner(ChangeOwnerRequestDto dto);
  void terminate(TerminateRequestDto dto);
  void changeFeeProduct(ChangeFeeProductRequestDto dto);
}
