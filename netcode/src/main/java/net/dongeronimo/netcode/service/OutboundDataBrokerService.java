package net.dongeronimo.netcode.service;

import org.springframework.stereotype.Service;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.dongeronimo.netcode.vo.OutboundDataVO;

@Service
public class OutboundDataBrokerService {
	public final BehaviorSubject<OutboundDataVO> observer;
	
	public OutboundDataBrokerService(BehaviorSubject<OutboundDataVO> obs) {
		observer = obs;
	}
	
	public void push(OutboundDataVO outboundDataVO) {
		observer.onNext(outboundDataVO);
		
	}
    
}
