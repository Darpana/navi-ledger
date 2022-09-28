package navi;


import navi.dto.response.BalanceResponse;
import navi.service.RequestProcessorService;
import navi.service.RepositoryClearService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BankingTest {

    @Autowired
    private RequestProcessorService requestProcessorService;

    @Autowired
    private RepositoryClearService repositoryClearService;

    @Before
    public void clearRepositoryBefore(){
        repositoryClearService.delete();
    }

    @After
    public void clearRepositoryAfter(){
        repositoryClearService.delete();
    }


    @Test
    public void addLoantest(){
        //Adding a loan of 5000 for 6% interest for a year
        requestProcessorService.executeRequest("LOAN IDIDI Dale 5000 1 6");
        BalanceResponse processBalance = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 3").get(0).getData();
        Assert.assertEquals("IDIDI Dale 1326.0 9", processBalance.getBankName() + " " + processBalance.getUserName()
                + " " + processBalance.getAmountPaid() + " " + processBalance.getEmisLeft());
    }

    @Test
    public void addPaymentTest(){
        requestProcessorService.executeRequest("LOAN IDIDI Dale 5000 1 6");
        BalanceResponse processBalance = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 3").get(0).getData();
        Assert.assertEquals("IDIDI Dale 1326.0 9", processBalance.getBankName() + " " + processBalance.getUserName()
                + " " + processBalance.getAmountPaid() + " " + processBalance.getEmisLeft());
        requestProcessorService.executeRequest("PAYMENT IDIDI Dale 1000 5");
        BalanceResponse balanceAfterPayment = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 12").get(0).getData();
        Assert.assertEquals("IDIDI Dale 5300.0 0", balanceAfterPayment.getBankName() + " " + balanceAfterPayment.getUserName()
                + " " + balanceAfterPayment.getAmountPaid() + " " + balanceAfterPayment.getEmisLeft());
    }

    @Test
    public void addPaymentAfterLoanCompletionTest(){
        requestProcessorService.executeRequest("LOAN IDIDI Dale 5000 1 6");
        BalanceResponse processBalance = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 3").get(0).getData();
        Assert.assertEquals("IDIDI Dale 1326.0 9", processBalance.getBankName() + " " + processBalance.getUserName()
                + " " + processBalance.getAmountPaid() + " " + processBalance.getEmisLeft());
        requestProcessorService.executeRequest("PAYMENT IDIDI Dale 1000 5");
        BalanceResponse balanceAfterPayment = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 12").get(0).getData();
        Assert.assertEquals("IDIDI Dale 5300.0 0", balanceAfterPayment.getBankName() + " " + balanceAfterPayment.getUserName()
                + " " + balanceAfterPayment.getAmountPaid() + " " + balanceAfterPayment.getEmisLeft());
        try {
            requestProcessorService.executeRequest("PAYMENT IDIDI Dale 1000 13");
        } catch (Exception ex){
            Assert.assertEquals("Payment not allowed since loan would have already closed by 13 months", ex.getMessage());
        }

    }

    @Test
    public void processAllBankingTest(){
        requestProcessorService.executeRequest("LOAN IDIDI Dale 5000 1 6");
        requestProcessorService.executeRequest("LOAN MBI Harry 10000 3 7");
        requestProcessorService.executeRequest("LOAN UON Shelly 15000 2 9");
        requestProcessorService.executeRequest("PAYMENT IDIDI Dale 1000 5");
        requestProcessorService.executeRequest("PAYMENT MBI Harry 5000 10");
        requestProcessorService.executeRequest("PAYMENT UON Shelly 7000 12");
        BalanceResponse balance1 = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 3").get(0).getData();
        BalanceResponse balance2 = (BalanceResponse) requestProcessorService.executeRequest("BALANCE IDIDI Dale 6").get(0).getData();
        BalanceResponse balance3 = (BalanceResponse) requestProcessorService.executeRequest("BALANCE UON Shelly 12").get(0).getData();
        BalanceResponse balance4 = (BalanceResponse) requestProcessorService.executeRequest("BALANCE MBI Harry 12").get(0).getData();

        Assert.assertEquals("IDIDI Dale 1326.0 9", balance1.getBankName() + " " + balance1.getUserName()
                + " " + balance1.getAmountPaid() + " " + balance1.getEmisLeft());
        Assert.assertEquals("IDIDI Dale 3652.0 4", balance2.getBankName() + " " + balance2.getUserName()
                + " " + balance2.getAmountPaid() + " " + balance2.getEmisLeft());
        Assert.assertEquals("UON Shelly 15856.0 3", balance3.getBankName() + " " + balance3.getUserName()
                + " " + balance3.getAmountPaid() + " " + balance3.getEmisLeft());
        Assert.assertEquals("MBI Harry 9044.0 10", balance4.getBankName() + " " + balance4.getUserName()
                + " " + balance4.getAmountPaid() + " " + balance4.getEmisLeft());
    }

}
