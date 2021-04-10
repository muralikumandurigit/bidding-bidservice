package murali.bidder.bid.entity;

import java.time.LocalDateTime;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("bids")
public class Bid {

	@PrimaryKey
	@Column
	private String bid;
	
	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime bid_time;
	
	@Column
	private String email;
	
	@Column
	private int old_price;
	
	@Column
	private int new_price;
	
	@Column
	private String sid;
}
