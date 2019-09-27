package com.cg.provisionService.grpc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.lognet.springboot.grpc.GRpcService;

import com.cg.grpc.contable.ContableServiceGrpc.ContableServiceImplBase;
import com.cg.grpc.contable.Request;
import com.cg.grpc.contable.Response;

import io.grpc.stub.StreamObserver;

@GRpcService
public class ContableServiceImpl extends ContableServiceImplBase{
	
public void creation(Request req,StreamObserver<Response> res) {
		
		Response.Builder build = Response.newBuilder();
		Date date = new Date(req.getTimestamp());
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd yyyy");  
	    String strDate= formatter.format(date);   
		String msg = "Cierre contable creado para el periodo "+strDate;
		System.out.println("message "+msg);
		build.setMsg(msg);
		Response b = build.build();
		res.onNext(b);
		res.onCompleted();
		
	}

}
