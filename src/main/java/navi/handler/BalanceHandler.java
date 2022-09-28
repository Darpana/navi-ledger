package navi.handler;

import navi.dto.Bank;
import navi.dto.Ledger;
import navi.dto.LoanApplication;
import navi.dto.User;
import navi.dto.enums.BankingRequestStatus;
import navi.dto.enums.CommandType;
import lombok.extern.slf4j.Slf4j;
import navi.dto.response.BalanceResponse;
import navi.dto.response.BaseResponse;
import navi.exceptions.InvalidInputException;
import navi.service.BankingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import navi.repository.BankRepository;
import navi.repository.LedgerRepository;
import navi.repository.LoanApplicationRepository;
import navi.repository.UserRepository;

import java.util.regex.Matcher;


@Component("balanceHandler")
@Slf4j
public class BalanceHandler extends BankingRequest {

    @Override
    public String getType() {
        return CommandType.BALANCE.name();
    }

    private String bankName;

    private String borrowerName;

    private Integer emiNo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    LedgerRepository ledgerRepository;

    @Override
    public void setParameter(Matcher matcher) throws InvalidInputException{
        clearData();
        int index = 1;
        try {
            while (matcher.find()) {
                switch (index) {
                    case 1 :
                        bankName = matcher.group();
                        break;
                    case 2 :
                        borrowerName = matcher.group();
                        break;
                    case 3 :
                        emiNo = Integer.parseInt(matcher.group());
                        if(emiNo < 0){
                            throw new InvalidInputException("Emi no cannot be less than 0");
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + index);
                }
                index++;
            }
        } catch (Exception e) {
            throw new InvalidInputException("Errored setParameter for value : " + matcher.group() + " " + e.getMessage());
        }
    }

    @Override
    public BaseResponse processInput() throws Exception{
        Bank bank = bankRepository.findByBankName(bankName);
        User user = userRepository.findByName(borrowerName);
        LoanApplication loanApplication = null;
        if (bank == null || user == null) {
            throw new Exception("Unknown user " + borrowerName + " or bank " + bankName);
        } else {
            loanApplication = loanApplicationRepository.findByUserIdAndBankId(user.getId(), bank.getId());
            if (loanApplication == null) {
                throw new Exception("No loan exists for user " + borrowerName + " with bank " + bankName);
            }
        }
        Ledger emi = ledgerRepository.findTopByLoanApplicationIdAndPaidEmiCountEqualsOrderByTxnNoDesc(loanApplication.getId(), emiNo);
        Double amountPaid = emi == null ? loanApplication.getTotalAmount() : loanApplication.getTotalAmount() - emi.getOutstandingBalance();
        Integer emisLeft = emi == null ? 0 : emi.getEmisLeft();
        return new BaseResponse(BankingRequestStatus.SUCCESS.name(), getResponse(bank.getBankName(), user.getName(), amountPaid, emisLeft));
    }

    private BalanceResponse getResponse(String bankName, String userName, Double amountPaid, Integer emiLeft){
        return BalanceResponse.builder()
                .amountPaid(amountPaid)
                .bankName(bankName)
                .userName(userName)
                .emisLeft(emiLeft)
                .build();
    }

    private void clearData() {
        bankName = null;
        borrowerName = null;
        emiNo = null;
    }
}
