package murali.bidder.bid.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyClass
public class ParticipateKey {
	
	@PrimaryKeyColumn(ordinal=0, type=PrimaryKeyType.PARTITIONED)
	private String email;
	
	@PrimaryKeyColumn(ordinal=1, type=PrimaryKeyType.PARTITIONED)
	private String sid;
	
	@Override
	public int hashCode() {
		return (email + " - " + sid).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParticipateKey pkey = (ParticipateKey)obj;
		if ((email == null && pkey.getEmail() != null) ||
			(email != null && pkey.getEmail() == null) ||
			(!email.equals(pkey.getEmail()))) {
			return false;
		}
		
		if ((sid == null && pkey.getSid() != null) ||
			(sid != null && pkey.getSid() == null) ||
			(!sid.equals(pkey.getSid()))) {
			return false;
			}
		return true;
	}
}
