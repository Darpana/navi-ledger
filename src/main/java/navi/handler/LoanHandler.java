package navi.handler;

import lombok.extern.slf4j.Slf4j;
import navi.dto.Bank;
import navi.dto.LoanApplication;
import navi.dto.User;
import navi.dto.enums.BankingRequestStatus;
import navi.dto.enums.CommandType;
import navi.dto.response.BaseResponse;
import navi.exceptions.DuplicateLoanException;
import navi.exceptions.InvalidInputException;
import navi.repository.BankRepository;
import navi.repository.LoanApplicationRepository;
import navi.repository.UserRepository;
import navi.service.BankingRequest;
import navi.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;



@Component("loanHandler")
@Slf4j
public class LoanHandler extends BankingRequest {

    @Override
    public String getType() {
        return CommandType.LOAN.name();
    }

    private String bankName;

    private String borrowerName;

    private Double principalAmount;

    private Integer tenure;

    private Double roi;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    LoanApplicationRepository loanApplicationRepository;

    @Autowired
    LedgerService ledgerService;

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
                        principalAmount = Double.parseDouble(matcher.group());
                        break;
                    case 4 :
                        tenure = Integer.parseInt(matcher.group());
                        if(tenure < 0){
                            throw new InvalidInputException("Tenure cannot be less than 0");
                        }
                        break;
                    case 5 :
                        roi = Double.parseDouble(matcher.group());
                        if(roi < 0){
                            throw new InvalidInputException("ROI cannot be less than 0");
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + paramIndex);
                }
                paramIndex++;
            }
        } catch (Exception e) {
            throw new Exception("Failed to parse BankingRequest for LOAN command : " + matcher.group() + " " + e.getMessage());
        }
    }

    @Override
    public BaseResponse processInput() throws DuplicateLoanException {
        User user = userRepository.findByName(borrowerName);
        Bank bank = bankRepository.findByBankName(bankName);
        if (user == null) {
            user = userRepository.save(User.builder()
                    .name(borrowerName)
                    .build());
        }
        if (bank == null) {
            bank = bankRepository.save(Bank.builder()
                    .bankName(bankName)
                    .build());
        }
        LoanApplication loanApplication = loanApplicationRepository.findByUserIdAndBankId(user.getId(), bank.getId());
        if (loanApplication != null) {
            throw new DuplicateLoanException("Loan already exists for user " + borrowerName + " and bank " + bankName);
        }
        loanApplication = LoanApplication.builder()
                .bankId(bank.getId())
                .userId(user.getId())
                .principalAmount(principalAmount)
                .roi(roi)
                .tenure(tenure)
                .build();
        loanApplication.setInterestAmount(calculateInterest(loanApplication));
        loanApplication.setTotalAmount(loanApplication.getPrincipalAmount() + loanApplication.getInterestAmount());
        loanApplication.setEmi(Math.ceil(loanApplication.getTotalAmount() / (loanApplication.getTenure() * 12)));

        //kept as synchronized to keep loan updation is synchronous
        synchronized (this) {
            loanApplication = loanApplicationRepository.save(loanApplication);
            ledgerService.fillLedger(loanApplication);
        }
        return new BaseResponse(BankingRequestStatus.SUCCESS.name(), loanApplication);
    }

    public Double calculateInterest(LoanApplication loanApplication) {
        return (loanApplication.getPrincipalAmount() * loanApplication.getRoi() * loanApplication.getTenure()) / 100;
    }

    private void clearData() {
        bankName = null;
        borrowerName = null;
        principalAmount = null;
        tenure = null;
        roi = null;
    }
}
