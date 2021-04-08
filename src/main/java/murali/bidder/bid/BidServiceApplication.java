package murali.bidder.bid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BidServiceApplication {

	@Value("${bid.service.job.threads}")
	private Integer threadCount;
	
	public static void main(String[] args) {
		SpringApplication.run(BidServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public ExecutorService executors() {
		return Executors.newFixedThreadPool(threadCount);
	}
}
