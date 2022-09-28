package navi.dto.response;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BalanceResponse {
    String bankName;
    String userName;
    Double amountPaid;
    Integer emisLeft;
}
