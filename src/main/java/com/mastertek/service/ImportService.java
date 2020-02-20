package com.mastertek.service;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastertek.config.ApplicationProperties;
import com.mastertek.repository.PersonRepository;

@Service
@Transactional
public class ImportService {

    private final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final AyonixEngineService ayonixEngineService;
    
    private final PersonRepository personRepository;
    
    private final ApplicationProperties applicationProperties;
    
    public ImportService(AyonixEngineService ayonixEngineService,PersonRepository personRepository,ApplicationProperties applicationProperties) {
		super();
		this.ayonixEngineService = ayonixEngineService;
		this.personRepository = personRepository;
		this.applicationProperties = applicationProperties;
	}

	public void startImport() throws Exception {
    	File f = new File(applicationProperties.getFilePath());
        System.out.println("Reading files using Apache IO:");
        List<String> lines = FileUtils.readLines(f, "UTF-8");

        Long startPoint = personRepository.count();
        ExecutorService executor = Executors.newFixedThreadPool(500);
        long countTemp = 0;
        for (int i = startPoint.intValue(); i < lines.size(); i++) {
			String line = lines.get(i);
			String[] params = line.split(",");
			ImageAnalyzeThread worker = new ImageAnalyzeThread(params,ayonixEngineService,personRepository);
			executor.execute(worker);
			countTemp++;
			System.out.println("Delele files thread -->" + i + ",count-->" + countTemp);
        }
        
        executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);

    }
    
	
}
