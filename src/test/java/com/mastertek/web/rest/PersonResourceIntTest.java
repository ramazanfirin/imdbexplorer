package com.mastertek.web.rest;

import com.mastertek.ImdbexplorerApp;

import com.mastertek.domain.Person;
import com.mastertek.repository.PersonRepository;
import com.mastertek.service.ImportService;
import com.mastertek.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.mastertek.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mastertek.domain.enumeration.RecordStatus;
/**
 * Test class for the PersonResource REST controller.
 *
 * @see PersonResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImdbexplorerApp.class)
public class PersonResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INDEX = "AAAAAAAAAA";
    private static final String UPDATED_INDEX = "BBBBBBBBBB";

    private static final RecordStatus DEFAULT_STATUS = RecordStatus.NO_FACE_DETECTED;
    private static final RecordStatus UPDATED_STATUS = RecordStatus.NO_AFID_DETECTED;

    private static final byte[] DEFAULT_AFID = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AFID = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_AFID_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AFID_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_INSERT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INSERT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PATH = "BBBBBBBBBB";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private ImportService importService;
    
    private MockMvc restPersonMockMvc;

    private Person person;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PersonResource personResource = new PersonResource(personRepository,importService);
        this.restPersonMockMvc = MockMvcBuilders.standaloneSetup(personResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createEntity(EntityManager em) {
        Person person = new Person()
            .name(DEFAULT_NAME)
            .index(DEFAULT_INDEX)
            .status(DEFAULT_STATUS)
            .afid(DEFAULT_AFID)
            .afidContentType(DEFAULT_AFID_CONTENT_TYPE)
            .insert(DEFAULT_INSERT)
            .path(DEFAULT_PATH);
        return person;
    }

    @Before
    public void initTest() {
        person = createEntity(em);
    }

    @Test
    @Transactional
    public void createPerson() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();

        // Create the Person
        restPersonMockMvc.perform(post("/api/people")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isCreated());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate + 1);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getIndex()).isEqualTo(DEFAULT_INDEX);
        assertThat(testPerson.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPerson.getAfid()).isEqualTo(DEFAULT_AFID);
        assertThat(testPerson.getAfidContentType()).isEqualTo(DEFAULT_AFID_CONTENT_TYPE);
        assertThat(testPerson.getInsert()).isEqualTo(DEFAULT_INSERT);
        assertThat(testPerson.getPath()).isEqualTo(DEFAULT_PATH);
    }

    @Test
    @Transactional
    public void createPersonWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();

        // Create the Person with an existing ID
        person.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonMockMvc.perform(post("/api/people")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = personRepository.findAll().size();
        // set the field null
        person.setName(null);

        // Create the Person, which fails.

        restPersonMockMvc.perform(post("/api/people")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isBadRequest());

        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPeople() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList
        restPersonMockMvc.perform(get("/api/people?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].index").value(hasItem(DEFAULT_INDEX.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].afidContentType").value(hasItem(DEFAULT_AFID_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].afid").value(hasItem(Base64Utils.encodeToString(DEFAULT_AFID))))
            .andExpect(jsonPath("$.[*].insert").value(hasItem(DEFAULT_INSERT.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH.toString())));
    }

    @Test
    @Transactional
    public void getPerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get the person
        restPersonMockMvc.perform(get("/api/people/{id}", person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(person.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.index").value(DEFAULT_INDEX.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.afidContentType").value(DEFAULT_AFID_CONTENT_TYPE))
            .andExpect(jsonPath("$.afid").value(Base64Utils.encodeToString(DEFAULT_AFID)))
            .andExpect(jsonPath("$.insert").value(DEFAULT_INSERT.toString()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPerson() throws Exception {
        // Get the person
        restPersonMockMvc.perform(get("/api/people/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);
        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Update the person
        Person updatedPerson = personRepository.findOne(person.getId());
        // Disconnect from session so that the updates on updatedPerson are not directly saved in db
        em.detach(updatedPerson);
        updatedPerson
            .name(UPDATED_NAME)
            .index(UPDATED_INDEX)
            .status(UPDATED_STATUS)
            .afid(UPDATED_AFID)
            .afidContentType(UPDATED_AFID_CONTENT_TYPE)
            .insert(UPDATED_INSERT)
            .path(UPDATED_PATH);

        restPersonMockMvc.perform(put("/api/people")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPerson)))
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getIndex()).isEqualTo(UPDATED_INDEX);
        assertThat(testPerson.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPerson.getAfid()).isEqualTo(UPDATED_AFID);
        assertThat(testPerson.getAfidContentType()).isEqualTo(UPDATED_AFID_CONTENT_TYPE);
        assertThat(testPerson.getInsert()).isEqualTo(UPDATED_INSERT);
        assertThat(testPerson.getPath()).isEqualTo(UPDATED_PATH);
    }

    @Test
    @Transactional
    public void updateNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Create the Person

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPersonMockMvc.perform(put("/api/people")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isCreated());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);
        int databaseSizeBeforeDelete = personRepository.findAll().size();

        // Get the person
        restPersonMockMvc.perform(delete("/api/people/{id}", person.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Person.class);
        Person person1 = new Person();
        person1.setId(1L);
        Person person2 = new Person();
        person2.setId(person1.getId());
        assertThat(person1).isEqualTo(person2);
        person2.setId(2L);
        assertThat(person1).isNotEqualTo(person2);
        person1.setId(null);
        assertThat(person1).isNotEqualTo(person2);
    }
}
