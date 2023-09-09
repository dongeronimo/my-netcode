package net.dongeronimo.netcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.dongeronimo.netcode.vo.OutboundDataVO;

@SpringBootApplication
public class NetcodeApplication {
	@Bean
	public BehaviorSubject<OutboundDataVO> outboundDataObserver(){
		BehaviorSubject<OutboundDataVO> obs = BehaviorSubject.create();
		return obs;
	}
	public static void main(String[] args) {
		SpringApplication.run(NetcodeApplication.class, args);
	}

}
