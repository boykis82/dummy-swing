package me.realimpact.dummy.swing.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.realimpact.dummy.swing.exception.policy.BusinessExceptionPolicy;
import org.springframework.http.HttpStatus;

/**
 * Defines the business exception reasons.
 */
@Getter
@AllArgsConstructor
public enum BusinessExceptionReason implements BusinessExceptionPolicy {
  
  SERVICE_NOT_FOUND_BY_EXT_REF("주어진 서비스관리번호로 서비스가 존재하지 않습니다! svc_mgmt_num = (%d)", HttpStatus.BAD_REQUEST),
  CUSTOMER_NOT_FOUND_BY_EXT_REF("주어진 얼마고고객ID로 얼마고고객이 존재하지 않습니다! olmago_custmoer_id = (%d)", HttpStatus.BAD_REQUEST),
  SERVICE_OLMAGO_RELATION_EXISTED("유효한 서비스-얼마고 고객 관계이력이 존재합니다! 먼저 연결을 끊어주세요! svc_mgmt_num = (%d) olmago_custmoer_id = (%d)", HttpStatus.CONFLICT),
  SERVICE_OLMAGO_RELATION_NOT_EXISTED("주어진 서비스-얼마고 고객 관계이력이 존재하지 않습니다! svc_mgmt_num = (%d) olmago_custmoer_id = (%d)", HttpStatus.NOT_FOUND),
  CUSTOMER_MISMATCH("서비스의 명의고객과 얼마고고객에 연결된 고객 불일치! nm_cust_num = (%d) olmago_cust_id = (%d) olmago_cust_num = (%d)", HttpStatus.PRECONDITION_FAILED)
  ;

  private final String code = BusinessExceptionReason.class.getSimpleName();
  private final String message;
  private final HttpStatus httpStatus;

}
