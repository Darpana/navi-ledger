package navi.service;

import lombok.extern.slf4j.Slf4j;
import navi.dto.response.BaseResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RequestProcessorService {

    public List<BaseResponse> executeRequest(String data) {
        try {
            if (data != null) {
                String[] inputs = data.split("\n");
                List<BaseResponse> response = new ArrayList<>();
                for (String input : inputs) {
                    BaseResponse processedOutput = processIndividualInput(input);
                    if (processedOutput != null) {
                        response.add(processedOutput);
                    }
                }
                return response;
            } else {
                log.error("Empty data received");
            }
        }catch (Exception e) {
            log.error("Failed to process data : {}, ", data, e);
        }
        return null;
    }

    private BaseResponse processIndividualInput(String input) {
        log.debug("Processing started for input : {}", input);
        BankingRequest bankingRequest;
        try {
            bankingRequest = ParserService.getBankingRequest(input);
            log.debug("Input : {}", input);
        }catch (Exception e) {
            log.error("Error input : {} with exception ", input, e);
            return null;
        }
        try {
            BaseResponse processedResult =  bankingRequest.processInput();
            log.debug("Processed input : {}", input);
            return processedResult;
        }catch (Exception e) {
            log.error("Errored : {} with exception ", input, e);
        }
        return null;
    }
}
