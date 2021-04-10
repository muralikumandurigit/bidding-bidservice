package murali.bidder.bid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import murali.bidder.bid.entity.Bid;
import murali.bidder.bid.service.BidService;

@RestController
@Slf4j
@RequestMapping("/bids")
public class BidController {

	@Autowired
	private BidService bidService;
	
	@PostMapping("/")
	public Bid saveBid(@RequestBody Bid bid) {
		log.info("Saving bid " + bid.toString());
		return bidService.saveBid(bid);
	}
	
	@PostMapping("/seedbid")
	public Bid saveSeedBid(@RequestBody Bid bid) {
		log.info("Saving seed bid " + bid.toString());
		return bidService.saveSeedBid(bid);
	}
	
//	@DeleteMapping("/delete")
	public void deleteBid(@PathVariable String bid) {
		log.info("Deleting bid " + bid.toString());
		bidService.deleteBid(bid);
	}
}
