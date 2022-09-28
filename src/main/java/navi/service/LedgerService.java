package navi.service;

import navi.dto.Ledger;
import navi.dto.LoanApplication;
import navi.dto.Payments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import navi.repository.LedgerRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LedgerService {

    @Autowired
    LedgerRepository ledgerRepository;

    @Transactional
    public void fillLedger(LoanApplication loanApplication) {
        List<Ledger> ledgers = new ArrayList<>();
        Double outstandingBalance = loanApplication.getTotalAmount();
        Integer emisLeft = loanApplication.getTenure() * 12;
        ledgers.add(Ledger.builder()
                .loanApplicationId(loanApplication.getId())
                .amount(0d)
                .outstandingBalance(outstandingBalance)
                .txnNo(0)
                .paidEmiCount(0)
                .emisLeft(emisLeft)
                .build());
        for (int i = 0; i < loanApplication.getTenure() * 12; i++) {
            outstandingBalance = outstandingBalance - loanApplication.getEmi();
            emisLeft--;
            Ledger ledger = Ledger.builder()
                    .loanApplicationId(loanApplication.getId())
                    .amount(outstandingBalance < 0 ? loanApplication.getEmi() + outstandingBalance : loanApplication.getEmi())
                    .outstandingBalance(outstandingBalance < 0 ? 0 : outstandingBalance)
                    .txnNo(i + 1)
                    .paidEmiCount(i + 1)
                    .emisLeft(emisLeft)
                    .build();
            ledgers.add(ledger);
        }
        ledgerRepository.saveAll(ledgers);
    }

    @Transactional
    public void modifyLedger(LoanApplication loanApplication, Payments payment) {
        List<Ledger> ledgers = ledgerRepository.findByLoanApplicationIdAndPaidEmiCountGreaterThanOrderByTxnNoAsc(loanApplication.getId(),
                Integer.parseInt(payment.getPaymentReference()));
        Ledger nextEmi = ledgers.get(0);
        Integer emisLeft = nextEmi.getEmisLeft();
        Double outstandingBalance = nextEmi.getOutstandingBalance() + nextEmi.getAmount();
        boolean isPaymentProcessed = false;
        for (Ledger ledger : ledgers) {
            if (outstandingBalance == 0) {
                ledger.setRedundant(true);
                continue;
            }
            if (!isPaymentProcessed) {
                outstandingBalance = outstandingBalance - payment.getAmount();
                emisLeft = emisLeft - (int) Math.floor((payment.getAmount() / loanApplication.getEmi())) + 1;
                ledger.setAmount(payment.getAmount());
                isPaymentProcessed = true;
            } else {
                emisLeft--;
                outstandingBalance = outstandingBalance - loanApplication.getEmi();
                ledger.setAmount(outstandingBalance < 0 ? loanApplication.getEmi() + outstandingBalance : loanApplication.getEmi());
            }
            ledger.setEmisLeft(emisLeft);
            outstandingBalance = outstandingBalance < 0 ? 0 : outstandingBalance;
            ledger.setOutstandingBalance(outstandingBalance);
            ledger.setPaidEmiCount(ledger.getPaidEmiCount() - 1);
        }
        ledgerRepository.saveAll(ledgers.stream().filter(a -> !a.isRedundant()).collect(Collectors.toList()));
        ledgerRepository.deleteByIdIn(ledgers.stream().filter(Ledger::isRedundant).map(Ledger::getId).collect(Collectors.toList()));
    }
}
