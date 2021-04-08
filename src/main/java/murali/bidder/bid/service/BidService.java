package murali.bidder.bid.service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import murali.bidder.bid.entity.Bid;
import murali.bidder.bid.repository.BidRepository;

@Service
public class BidService {

	@Autowired
	private BidRepository bidRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	ExecutorService executors;

	@Value("${user.service.endpoint}")
	private String userServiceEndpoint;
	
	@Value("${seed.service.endpoint}")
	private String seedServiceEndpoint;
	
	private Boolean isValidUser(String email) {
		return restTemplate.getForObject(userServiceEndpoint + email, Boolean.class);
	}
	
	private Boolean isValidSeed(String sid) {
		return restTemplate.getForObject(seedServiceEndpoint + sid, Boolean.class);
	}
	
	private void checkForBidValidity(Bid bid) {
		Future<Boolean> isValidUserFutureObject = executors.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return isValidUser(bid.getEmail());
			}
			
		});
		
		Future<Boolean> isValidSeedFutureObject = executors.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return isValidSeed(bid.getSid());
			}
			
		});

		try {
			if (!isValidUserFutureObject.get()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user exist with the email " + bid.getEmail());
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to reach userService " + e.getLocalizedMessage());
		}
		
		try {
			if (!isValidSeedFutureObject.get()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No seed exist with the id " + bid.getSid());
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to reach seedService " + e.getLocalizedMessage());
		}
		
	}
	
	public Bid saveSeedBid(Bid bid) {
		bid.setBid(UUID.randomUUID().toString());
		bid.setBid_time(new Date());
		return bidRepository.save(bid); 	
	}
	
	public Bid saveBid(Bid bid) {
		checkForBidValidity(bid);
		bid.setBid(UUID.randomUUID().toString());
		bid.setBid_time(new Date());
		return bidRepository.save(bid); 
	}

	public void deleteBid(String bid_id) {
		Bid bid = new Bid();
		bid.setBid(bid_id);
		bidRepository.delete(bid);
	}
}
