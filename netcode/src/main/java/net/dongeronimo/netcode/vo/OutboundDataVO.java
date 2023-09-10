package net.dongeronimo.netcode.vo;

import java.util.HashMap;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Data Carrier of Outbound Data Broker.
 * */
public class OutboundDataVO {
	public PayloadAssembler assembler;
	/**
	 * Hack, but makes the equality very easy to do
	 * */
	public final long id;
	/**
	 * What is this data? 
	 * */
	@NonNull
	public final String what;
	/**
	 * Who is the recipient of the data?
	 * */
	@NonNull
	public final String toWhom;
	/**
	 * What is the actual data?
	 * */
	@Nullable
	public final HashMap<String, Object> payload;
	//I know it's ugly but it solves the issue of comparison
	private static long idCounter = 0;
	
	public OutboundDataVO(long _id, @NonNull String _what, @NonNull String _toWhom, @Nullable HashMap<String, Object> _payload) {
		id = _id;
		this.what = _what;
		this.toWhom = _toWhom;
		this.payload = _payload;
	}
	
	public OutboundDataVO(@NonNull String _what, @NonNull String _toWhom, @Nullable HashMap<String, Object> _payload) {
		id = ++idCounter;
		this.what = _what;
		this.toWhom = _toWhom;
		this.payload = _payload;
	}
	@Override
	public boolean equals(Object other) {
		if(other.getClass() != this.getClass())
			return false;
		OutboundDataVO o = (OutboundDataVO)other;
		return o.id == this.id;
	}
	@Override
	public int hashCode() {
		return ((Long)id).hashCode();
	}

	public interface PayloadAssembler {
		String assemble();
	}
}
