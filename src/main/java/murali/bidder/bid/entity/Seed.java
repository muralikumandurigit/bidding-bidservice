package murali.bidder.bid.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seed {

	private String sid;
	
	private String cid;
	
	private String email;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime start_date;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime end_date;
	
	private Boolean increment;
	
	private String seed_bid;
	
	private String winner_email;
	
	private String status;
	
	private String winning_bid;
	
	private int seed_price;
}
