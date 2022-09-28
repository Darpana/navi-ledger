package navi.repository;

import navi.dto.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, String> {
    void deleteByIdIn(List<String> ids);
    List<Ledger> findByLoanApplicationIdAndPaidEmiCountGreaterThanOrderByTxnNoAsc(String loanApplicationId, Integer emiCount);
    Ledger findTopByLoanApplicationIdAndPaidEmiCountEqualsOrderByTxnNoDesc(String loanApplicationId, Integer emiNo);
}