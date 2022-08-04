package me.realimpact.dummy.swing.service;

import me.realimpact.dummy.swing.dto.*;

public interface MobilePhoneServiceService {
  ChangeOwnerResponseDto changeOwner(ChangeOwnerRequestDto dto);
  TerminateResponseDto terminate(TerminateRequestDto dto);
  ChangeFeeProductResponseDto changeFeeProduct(ChangeFeeProductRequestDto dto);
}
