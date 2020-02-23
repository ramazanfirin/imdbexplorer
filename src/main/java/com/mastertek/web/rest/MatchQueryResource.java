package com.mastertek.web.rest;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mastertek.domain.MatchQuery;
import com.mastertek.domain.Person;
import com.mastertek.repository.MatchQueryRepository;
import com.mastertek.repository.PersonRepository;
import com.mastertek.service.AyonixEngineService;
import com.mastertek.web.rest.errors.BadRequestAlertException;
import com.mastertek.web.rest.util.HeaderUtil;
import com.mastertek.web.rest.util.PaginationUtil;

import ayonix.AyonixFace;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing MatchQuery.
 */
@RestController
@RequestMapping("/api")
public class MatchQueryResource {

    private final Logger log = LoggerFactory.getLogger(MatchQueryResource.class);

    private static final String ENTITY_NAME = "matchQuery";

    private final MatchQueryRepository matchQueryRepository;
    
    List<Person> personList = null;
    
    private final PersonRepository personRepository;
    
    private final AyonixEngineService ayonixEngineService;

    public MatchQueryResource(MatchQueryRepository matchQueryRepository,PersonRepository personRepository,AyonixEngineService ayonixEngineService) {
        this.matchQueryRepository = matchQueryRepository;
        this.personRepository = personRepository;
        this.ayonixEngineService = ayonixEngineService;
    }

    /**
     * POST  /match-queries : Create a new matchQuery.
     *
     * @param matchQuery the matchQuery to create
     * @return the ResponseEntity with status 201 (Created) and with body the new matchQuery, or with status 400 (Bad Request) if the matchQuery has already an ID
     * @throws Exception 
     */
    @PostMapping("/match-queries")
    @Timed
    public ResponseEntity<MatchQuery> createMatchQuery(@RequestBody MatchQuery matchQuery) throws Exception {
        log.debug("REST request to save MatchQuery : {}", matchQuery);
        if (matchQuery.getId() != null) {
            throw new BadRequestAlertException("A new matchQuery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        //compare(matchQuery);
        compare2(matchQuery);
        
        MatchQuery result = matchQueryRepository.save(matchQuery);
        return ResponseEntity.created(new URI("/api/match-queries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    public List<Person> getPersonList(){
    	if(personList==null) {
    		personList = personRepository.findAll();
    	}
    	return personList;	
    }
    
    public void compare(MatchQuery matchQuery) throws Exception {
    	List<Person> getPersonList = getPersonList();
    	byte[] afid=getAfid(matchQuery);
    	
    	float result=0f;
    	Person person=null;
    	
    	Long startDate = System.currentTimeMillis();
    	
    	for (Iterator iterator = getPersonList.iterator(); iterator.hasNext();) {
			Person personTemp = (Person) iterator.next();
			float temp = ayonixEngineService.match(afid, personTemp.getAfid());
//			if(temp>result) {
//				result = temp;
//				person = personTemp;
//			}
    	}
    	
    	Long endDate = System.currentTimeMillis();
    	System.out.println("compare time : "+ (endDate - startDate));
    	
    	matchQuery.setPerson(person);
    	matchQuery.setResult(result);
    }
    
    public void compare2(MatchQuery matchQuery) throws Exception {
    	List<Person> personList = getPersonList();
    	byte[] afid=getAfid(matchQuery);
    	
    	Vector<byte[]> afids  = new Vector<byte[]>();
    	for (Iterator iterator = personList.iterator(); iterator.hasNext();) {
			Person bs = (Person) iterator.next();
			afids.add(bs.getAfid());
		}
    	
    	float[] scores = new float[personList.size()];
		int[] indexes = new int[personList.size()];
		
		Long startDate = System.currentTimeMillis();
    	ayonixEngineService.match(afid, afids,scores,indexes);
		Long endDate = System.currentTimeMillis();
    	System.out.println("compare time : "+ (endDate - startDate));

    	float maxScore =0f;
    	int index=0;
    	
    	for (int i = 0; i < scores.length; i++) {
			if(scores[i]>maxScore) {
				maxScore = scores[i];
				index= i;
			}
		}
    	
    	Long endDate2 = System.currentTimeMillis();
    	System.out.println("compare time : "+ (endDate2 - startDate));

    	matchQuery.setPerson(personList.get(index));
    	matchQuery.setResult(maxScore);
    	System.out.println("bitti");
    	
    	
    }
    
    public AyonixFace getFace(MatchQuery matchQuery) throws Exception {
    	AyonixFace[] faces=null;
    	if(!StringUtils.isEmpty(matchQuery.getUrl())) {
    		faces = ayonixEngineService.detectFaces(matchQuery.getUrl());
    	}
    	if(matchQuery.getImage()!=null) {
    		faces = ayonixEngineService.detectFaces(matchQuery.getImage());
    	    
    	}
    	
    	if(faces==null)
    		throw new RuntimeException("not suitable images");
    	
    	if(faces.length == 0)
    		throw new RuntimeException("no face detected");
   
     	if(faces.length > 1)
    		throw new RuntimeException("Multiple face detected. There must be one face.");
   
     	AyonixFace face = faces[0];
     	ayonixEngineService.preProcess(face);
     	return face;
    }
    
    public byte[] getAfid(MatchQuery matchQuery) throws Exception {
    	AyonixFace face = getFace(matchQuery);
    	byte[] afid = null;
    	try {
			afid = ayonixEngineService.createTemplate(face);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("afid cant detect");
		}
    	
    	return afid;
    }
    
    /**
     * PUT  /match-queries : Updates an existing matchQuery.
     *
     * @param matchQuery the matchQuery to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated matchQuery,
     * or with status 400 (Bad Request) if the matchQuery is not valid,
     * or with status 500 (Internal Server Error) if the matchQuery couldn't be updated
     * @throws Exception 
     */
    @PutMapping("/match-queries")
    @Timed
    public ResponseEntity<MatchQuery> updateMatchQuery(@RequestBody MatchQuery matchQuery) throws Exception {
        log.debug("REST request to update MatchQuery : {}", matchQuery);
        if (matchQuery.getId() == null) {
            return createMatchQuery(matchQuery);
        }
        MatchQuery result = matchQueryRepository.save(matchQuery);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, matchQuery.getId().toString()))
            .body(result);
    }

    /**
     * GET  /match-queries : get all the matchQueries.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of matchQueries in body
     */
    @GetMapping("/match-queries")
    @Timed
    public ResponseEntity<List<MatchQuery>> getAllMatchQueries(Pageable pageable) {
        log.debug("REST request to get a page of MatchQueries");
        Page<MatchQuery> page = matchQueryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/match-queries");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /match-queries/:id : get the "id" matchQuery.
     *
     * @param id the id of the matchQuery to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the matchQuery, or with status 404 (Not Found)
     */
    @GetMapping("/match-queries/{id}")
    @Timed
    public ResponseEntity<MatchQuery> getMatchQuery(@PathVariable Long id) {
        log.debug("REST request to get MatchQuery : {}", id);
        MatchQuery matchQuery = matchQueryRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(matchQuery));
    }

    /**
     * DELETE  /match-queries/:id : delete the "id" matchQuery.
     *
     * @param id the id of the matchQuery to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/match-queries/{id}")
    @Timed
    public ResponseEntity<Void> deleteMatchQuery(@PathVariable Long id) {
        log.debug("REST request to delete MatchQuery : {}", id);
        matchQueryRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
