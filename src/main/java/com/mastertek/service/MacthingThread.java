package com.mastertek.service;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacthingThread implements Runnable{
		private final Logger log = LoggerFactory.getLogger(MacthingThread.class);
		AyonixEngineService ayonixEngineService;
		byte[] afid;
		Vector<byte[]> afids=new Vector<byte[]>();
		
	    public MacthingThread(AyonixEngineService ayonixEngineService,byte[] afid,List<byte[]> afids){  
	        this.ayonixEngineService = ayonixEngineService;
	        this.afid = afid;
	        this.afids.addAll(afids);
	    	
	    }  
	   
		@Override
		public void run() {
			float[] scores = new float[afids.size()];
			int[] indexes = new int[afids.size()];
			
			Long startDate = System.currentTimeMillis();
	    	ayonixEngineService.match(afid, afids,scores,indexes);
			Long endDate = System.currentTimeMillis();
			
			float maxScore =0f;
	    	int index=0;
	    	
	    	for (int i = 0; i < scores.length; i++) {
				if(scores[i]>maxScore) {
					maxScore = scores[i];
					index= i;
				}
			}
			
			
	    	log.info(Thread.currentThread().getName()+" tamamlandı. maxScore"+maxScore+".index:"+index+"compare time : "+ (endDate - startDate));
		}
}
