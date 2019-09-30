package com.cg.provisionService.grpc;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.cg.grpc.ProvisionServiceGrpc;
import com.cg.grpc.request;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Component
public class Client {

	
	private ProvisionServiceGrpc.ProvisionServiceBlockingStub stub;
	
	@PostConstruct
	private void init() {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("contable-service.adifv2.svc", 6565).usePlaintext(true).build();
		stub = ProvisionServiceGrpc.newBlockingStub(channel);
	}
	
	public String message(long ts,String codigo) {
		request res = request.newBuilder().setTimestamp(ts).setCodigo(codigo).build();
		
		String msg = stub.creation(res).getMsg();
		
		
		return msg;
		
	}
}
