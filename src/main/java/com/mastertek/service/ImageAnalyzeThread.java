package com.mastertek.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mastertek.domain.Person;
import com.mastertek.domain.enumeration.RecordStatus;
import com.mastertek.repository.PersonRepository;

import ayonix.AyonixFace;

public class ImageAnalyzeThread implements Runnable{
		private final Logger log = LoggerFactory.getLogger(ImageAnalyzeThread.class);
	
		String[] params;
		AyonixEngineService ayonixEngineService;
		PersonRepository personRepository;
		
	    public ImageAnalyzeThread(String[] params,AyonixEngineService ayonixEngineService,PersonRepository personRepository){  
	        this.ayonixEngineService = ayonixEngineService;
	    	this.personRepository = personRepository;
	    	this.params = params;
	    	
	    }  
	   
		public void run() {
			Person person = new Person();
			try {
				
				//person.setInsert(Instant.now());
				person.setName(params[0]);
				person.setIndex(params[1]);
				person.setPath(params[5]);
				
				List<AyonixFace> faces = ayonixEngineService.analyze(params[5]);
				if(faces.size()==0) {
					person.setStatus(RecordStatus.NO_FACE_DETECTED);
				}else {
					ayonixEngineService.preProcess(faces.get(0));
					try {
						byte[] template = ayonixEngineService.createTemplate(faces.get(0));
						person.setAfid(template);
						person.setAfidContentType("afidContentType");
						person.setStatus(RecordStatus.PROCESSED);
					} catch (Exception e) {
						person.setStatus(RecordStatus.NO_AFID_DETECTED);
					}
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info(e.getMessage());
				person.setStatus(RecordStatus.ERROR);
			}
			personRepository.saveAndFlush(person);
			
			log.info(Thread.currentThread().getName()+" tamamlandı");
		}
}
