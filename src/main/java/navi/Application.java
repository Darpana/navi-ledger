package navi;

import navi.exceptions.FileNotFoundException;
import navi.parsers.InputFileParserImpl;
import navi.parsers.InputParser;
import navi.util.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;


import navi.service.RequestProcessorService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * This is a Ledger application.
 *
 * It reads input from input.txt file.
 *
 * @author Darpana
 */

@SpringBootApplication
@ComponentScan(basePackages = {"navi.*"})
@EnableJpaRepositories(basePackages = {"navi.repository"})
@EntityScan("navi.dto")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		startLedger();
	}

	public static void startLedger() throws FileNotFoundException {
		RequestProcessorService processor = new RequestProcessorService();
		InputParser parser = InputFileParserImpl.getInstance();
		String request;
		while( (request = parser.getNextInput(Constants.INPUT_FILE)) != null){
			processor.executeRequest(request);
		}
		parser.closeSource(Constants.INPUT_FILE);

	}

}
