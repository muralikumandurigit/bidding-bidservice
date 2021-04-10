package murali.bidder.bid.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import murali.bidder.bid.entity.Bid;

@Repository
public interface BidRepository  extends CassandraRepository<Bid, String>{

	public Bid findByBid(String bid);

}
