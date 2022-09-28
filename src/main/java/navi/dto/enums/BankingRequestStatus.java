package navi.dto.enums;

import lombok.Getter;

@Getter
public enum BankingRequestStatus {
    SUCCESS,
    FAILED,
    INVALID,
    ALREADY_EXISTS
}
