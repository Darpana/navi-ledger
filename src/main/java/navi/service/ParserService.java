package navi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ParserService {
    private static final Map<String, BankingRequest> bankingRequestMap = new HashMap<>();


    @Autowired
    List<BankingRequest> bankingRequest;

    @PostConstruct
    public void initialiseRequest() {
        for (BankingRequest bankingRequest : bankingRequest) {
            bankingRequestMap.put(bankingRequest.getType(), bankingRequest);
        }
    }


    public static BankingRequest getBankingRequest(String input) throws Exception {
        Pattern pattern = Pattern.compile("\\S+");
        Matcher matcher = pattern.matcher(input);
        BankingRequest bankingRequest = null;
        if (matcher.find()) {
            bankingRequest = bankingRequestMap.get(matcher.group());
            if (bankingRequest == null) {
                log.error("Unknown command type : {}", matcher.group());
                throw new Exception("Unknown Command type : " + matcher.group());
            }
            bankingRequest.setParameter(matcher);
        }
        return bankingRequest;
    }
}
