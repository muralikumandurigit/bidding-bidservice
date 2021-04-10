package murali.bidder.bid.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import murali.bidder.bid.entity.Bid;
import murali.bidder.bid.entity.Participate;
import murali.bidder.bid.entity.Seed;
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
	
	@Value("{participate.service.endpoint}")
	private String participateServiceEndpoint;
	
	private Boolean isValidUser(String email) {
		return restTemplate.getForObject(userServiceEndpoint + email, Boolean.class);
	}
	
	private Bid getBid(String bid) {
		return bidRepository.findByBid(bid);
	}
	
	private Seed getSeed(String sid) {
		return restTemplate.getForObject(seedServiceEndpoint + sid, Seed.class);
	}
	
	private List<Participate> getParticipations(String email) {
		return restTemplate.getForObject(participateServiceEndpoint + email, List.class);
	}
	
	private Seed updateSeedWinningBidInSeed(Seed seed) {
		return restTemplate.postForObject(seedServiceEndpoint + "updatewinningbid", seed, Seed.class);
	}
	
	private Seed checkForBidValidityAndGetSeed(Bid bid) {
		Seed seed = null;
		Future<Boolean> isValidUserFutureObject = executors.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return isValidUser(bid.getEmail());
			}
			
		});
		
		Future<Seed> seedFutureObject = executors.submit(new Callable<Seed>() {

			@Override
			public Seed call() throws Exception {
				return getSeed(bid.getSid());
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
			seed = seedFutureObject.get();
			if (seed == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No seed exist with the id " + bid.getSid());
			}
			else if (seed.getStatus() != "N" && seed.getStatus() != "S") {
				throw new ResponseStatusException(HttpStatus.FOUND, "Seed already completed " + seed.getStatus());				
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to reach seedService " + e.getLocalizedMessage());
		}
		return seed;
	}
	
	public Bid saveSeedBid(Bid bid) {
		bid.setBid(UUID.randomUUID().toString());
		bid.setBid_time(LocalDateTime.now());
		return bidRepository.save(bid); 	
	}
	
	public Bid saveBid(Bid bid) {
		
		// Verify whether user is allowed to bid or not
		List<Participate> participateList = getParticipations(bid.getEmail());
		Optional<Participate> o =  participateList.stream().filter(m -> bid.getSid().equals(m.getParticipateKey().getSid())).findFirst();
		if (o.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not joined the seed " + bid.getSid());
		}
		
		synchronized(bid.getSid()) {
			Seed seed = checkForBidValidityAndGetSeed(bid);
			Bid latestBid = getBid(seed.getWinning_bid());
			if (seed.getIncrement() &&  bid.getNew_price() <= latestBid.getNew_price()) {
				throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You cannot quote for lower or equal price. Current latest price is " + latestBid.getNew_price());
			}
			else if (!seed.getIncrement() && bid.getNew_price() >= latestBid.getNew_price()) {
				throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You cannot quote for higher or equal price. Current latest price is " + latestBid.getNew_price());
			}
			bid.setBid(UUID.randomUUID().toString());
			bid.setBid_time(LocalDateTime.now());
			latestBid = bidRepository.save(bid);
			seed.setWinning_bid(latestBid.getBid());
			updateSeedWinningBidInSeed(seed);
			return latestBid;
		}
	}

	public void deleteBid(String bid_id) {
		Bid bid = new Bid();
		bid.setBid(bid_id);
		bidRepository.delete(bid);
	}
}
