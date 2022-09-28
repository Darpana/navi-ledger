package navi.repository;

import navi.dto.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Integer> {

    Bank findByBankName(String bankName);

}
