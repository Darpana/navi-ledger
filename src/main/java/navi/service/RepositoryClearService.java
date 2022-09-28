package navi.service;

import lombok.extern.slf4j.Slf4j;
import navi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RepositoryClearService {

        @Autowired
        UserRepository userRepository;

        @Autowired
        LedgerRepository ledgerRepository;

        @Autowired
        PaymentsRepository paymentsRepository;

        @Autowired
        LoanApplicationRepository loanApplicationRepository;

        @Autowired
        BankRepository bankRepository;

        public void delete() {
            userRepository.deleteAll();
            ledgerRepository.deleteAll();
            paymentsRepository.deleteAll();
            loanApplicationRepository.deleteAll();
        }
}
