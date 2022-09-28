package navi.repository;

import navi.dto.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String> {

    LoanApplication findByUserIdAndBankId(String userId, Integer bankId);
}
