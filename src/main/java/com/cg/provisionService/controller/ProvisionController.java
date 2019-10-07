package com.cg.provisionService.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.cg.adif.provision.modal.Provisionados;
import com.cg.provisionService.domain.Budget;
import com.cg.provisionService.domain.BudgetDb;
import com.cg.provisionService.domain.Composite;
//import com.cg.provisionService.domain.Customresponse;
import com.cg.provisionService.domain.Db;
import com.cg.provisionService.domain.Provision;
import com.cg.provisionService.domain.SearchCodigoTo;
import com.cg.provisionService.domain.customProvionTo;
import com.cg.provisionService.grpc.Client;
//import com.cg.provisionService.protobuff.MyClient;
import com.cg.provisionService.service.ProvisionService;

import io.opentracing.Span;
import io.opentracing.Tracer;

@RestController
@RequestMapping("adif")
public class ProvisionController {
	
	@Autowired
	ProvisionService svc;
	
	@Autowired
	private BudgetDb db;
	
	@Autowired
	private Db cod;
	
	@Autowired
	Client client;
	
	@Autowired
    private Tracer tracer;
	
	@CrossOrigin
	 @PostMapping("/codigo")
		ResponseEntity<Provision> createNew(@RequestBody customProvionTo c)
		{
		 Date date = new Date(c.getTimestamp());
		 Composite obj = new Composite();
		 obj.setCodigo(c.getCodigo_sap_expediente());
		 obj.setCod_sociedad(c.getCod_sociedad());
		 obj.setPeriodo(date);
		 Provision pro = new Provision();
		 pro.setKey(obj);
		 Provision con = svc.createNew(pro);
              if(con!=null) {
           	   Span span = tracer.buildSpan("Succesfully added a provision entry").start();
    			  span.finish();
		       Span span2 = tracer.buildSpan("Making a call to Contable microservice").start();
			      String msg = client.message(c.getTimestamp(),c.getCodigo_sap_expediente());
		       span2.finish();
			      Span span1 = tracer.buildSpan("Received the msg from contable microservice: "+msg).start();
    			  span1.finish();
			    return new ResponseEntity<>(pro, HttpStatus.OK);
		 }
			else
			    return new ResponseEntity<>(null, HttpStatus.CONFLICT);
		}
	
	
	@GetMapping("/getall")
	public List<Provision> getAll()
	{
		return svc.getAll();
		
	}
	
//	@CrossOrigin
//	@GetMapping("/list/{ts}/{page}/{size}")
//	public Customresponse listAll(@PathVariable("ts") long timestamp,@PathVariable("page") int page,@PathVariable("size") int size) {
//		
//		Customresponse res = new Customresponse();
//		System.out.println("before response");
//		return client.fetch(timestamp, page, size);
//	}
	
	@CrossOrigin
	 @GetMapping("/list/{ts}/{page}/{size}")
	 Page<Provision> listAll(@PathVariable("ts") long ts,@PathVariable("page") int page,@PathVariable("size") int size)
	 {
		Date date = new Date(ts);
		Pageable listAll = null;
	     listAll = PageRequest.of(page, size, Sort.by("codigo_sap_expediente").ascending());
		 return cod.findByPeriodo(date, listAll);
	   
		 
	 }
	
	  @CrossOrigin
	  @PostMapping("/codigo/delete")
	   void deleteCodigo(@RequestBody customProvionTo c)
	   {
		 Date date = new Date(c.getTimestamp());
		 Composite obj = new Composite();
		 obj.setCodigo(c.getCodigo_sap_expediente());
		 obj.setCod_sociedad(c.getCod_sociedad());
		 obj.setPeriodo(date);
		 Provision pro = new Provision();
		 pro.setKey(obj);
        svc.deleteById(pro);
	   }
	
	
	 @CrossOrigin
	 @PostMapping("/codigo/autosearch")
	 List<Budget> searchList(@RequestBody customProvionTo c){
		 String codigo_sap_expediente = c.getCodigo_sap_expediente().toLowerCase();

		 codigo_sap_expediente= "%"+codigo_sap_expediente+"%";
		return db.search(codigo_sap_expediente);

	 }
	 
	 @CrossOrigin
	 @PostMapping("/codigo/search")
	 Page<Provision> searchListAll(@RequestBody SearchCodigoTo c)
	 {
		 Pageable listAll = null;
	     listAll = PageRequest.of(c.getPage(),c.getSize(),Sort.by("codigo_sap_expediente").ascending());
	     Date date = new Date(c.getTimestamp());
	     String searchString = null;
	     c.setCodigo(c.getCodigo().toLowerCase());
	     searchString="%"+c.getCodigo()+"%";
		return cod.search(searchString,date,listAll);
		 
	 }
	
	
	 
	
	
}
