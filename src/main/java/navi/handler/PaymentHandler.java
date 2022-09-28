package navi.handler;

import navi.dto.*;
import navi.dto.enums.BankingRequestStatus;
import navi.dto.enums.CommandType;
import lombok.extern.slf4j.Slf4j;
import navi.dto.response.BaseResponse;
import navi.exceptions.InvalidInputException;
import navi.exceptions.LoanClosedException;
import navi.exceptions.LoanDoesNotExistsException;
import navi.service.BankingRequest;
import navi.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import navi.repository.BankRepository;
import navi.repository.LoanApplicationRepository;
import navi.repository.PaymentsRepository;
import navi.repository.UserRepository;

import java.util.regex.Matcher;


@Component("paymentHandler")
@Slf4j
public class PaymentHandler extends BankingRequest {

    private Integer MONTHS = 12;

    @Override
    public String getType() {
        return CommandType.PAYMENT.name();
    }

    private String bankName;

    private String borrowerName;

    private Double amount;

    private Integer emiNo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    PaymentsRepository paymentsRepository;

    @Override
    public void setParameter(Matcher matcher) throws Exception{
        clearData();
        int paramIndex = 1;
        try {
            while (matcher.find()) {
                switch (paramIndex) {
                    case 1 :
                        bankName = matcher.group();
                        break;
                    case 2 :
                        borrowerName = matcher.group();
                        break;
                    case 3 :
                        amount = Double.parseDouble(matcher.group());
                        if(amount < 0){
                            throw new InvalidInputException("Amount cannot be less than 0");
                        }
                        break;
                    case 4 :
                        emiNo = Integer.parseInt(matcher.group());
                        if(emiNo < 0){
                            throw new InvalidInputException("Emi No. cannot be less than 0");
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + paramIndex);
                }
                paramIndex++;
            }
        } catch (Exception e) {
            throw new Exception("Failed to parse BankingRequest PAYMENT command : " + matcher.group() + " " + e.getMessage());
        }
    }

    @Override
    public BaseResponse processInput() throws Exception{
        Bank bank = bankRepository.findByBankName(bankName);
        User user = userRepository.findByName(borrowerName);
        LoanApplication loanApplication;
        if (bank == null || user == null) {
            throw new Exception("Unknown user " + borrowerName + " or bank " + bankName);
        } else {
            loanApplication = loanApplicationRepository.findByUserIdAndBankId(user.getId(), bank.getId());
            if (loanApplication == null) {
                throw new LoanDoesNotExistsException("Loan does not exists for user " + borrowerName + " and bank " + bankName);
            }
        }
        if (emiNo >= loanApplication.getTenure() * MONTHS) {
            throw new LoanClosedException("Payment not allowed since loan would have already closed by " + emiNo + " months");
        }
        Payments payments = Payments.builder()
                .paymentReference(String.valueOf(emiNo))
                .amount(amount)
                .loanApplicationId(loanApplication.getId())
                .paymentType(PaymentType.LUMP_SUM.name())
                .build();

        synchronized (this){
            payments = paymentsRepository.save(payments);
            ledgerService.modifyLedger(loanApplication, payments);
        }
        return new BaseResponse(BankingRequestStatus.SUCCESS.name(), payments);
    }

    private void clearData() {
        bankName = null;
        borrowerName = null;
        amount = null;
        emiNo = null;
    }
}
