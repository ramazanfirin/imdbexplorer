package com.mastertek.web.rest;

import com.mastertek.ImdbexplorerApp;

import com.mastertek.domain.MatchQuery;
import com.mastertek.repository.MatchQueryRepository;
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
import java.util.List;

import static com.mastertek.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MatchQueryResource REST controller.
 *
 * @see MatchQueryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImdbexplorerApp.class)
public class MatchQueryResourceIntTest {

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Float DEFAULT_RESULT = 1F;
    private static final Float UPDATED_RESULT = 2F;

    @Autowired
    private MatchQueryRepository matchQueryRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMatchQueryMockMvc;

    private MatchQuery matchQuery;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MatchQueryResource matchQueryResource = new MatchQueryResource(matchQueryRepository);
        this.restMatchQueryMockMvc = MockMvcBuilders.standaloneSetup(matchQueryResource)
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
    public static MatchQuery createEntity(EntityManager em) {
        MatchQuery matchQuery = new MatchQuery()
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .url(DEFAULT_URL)
            .result(DEFAULT_RESULT);
        return matchQuery;
    }

    @Before
    public void initTest() {
        matchQuery = createEntity(em);
    }

    @Test
    @Transactional
    public void createMatchQuery() throws Exception {
        int databaseSizeBeforeCreate = matchQueryRepository.findAll().size();

        // Create the MatchQuery
        restMatchQueryMockMvc.perform(post("/api/match-queries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchQuery)))
            .andExpect(status().isCreated());

        // Validate the MatchQuery in the database
        List<MatchQuery> matchQueryList = matchQueryRepository.findAll();
        assertThat(matchQueryList).hasSize(databaseSizeBeforeCreate + 1);
        MatchQuery testMatchQuery = matchQueryList.get(matchQueryList.size() - 1);
        assertThat(testMatchQuery.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testMatchQuery.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testMatchQuery.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testMatchQuery.getResult()).isEqualTo(DEFAULT_RESULT);
    }

    @Test
    @Transactional
    public void createMatchQueryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = matchQueryRepository.findAll().size();

        // Create the MatchQuery with an existing ID
        matchQuery.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMatchQueryMockMvc.perform(post("/api/match-queries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchQuery)))
            .andExpect(status().isBadRequest());

        // Validate the MatchQuery in the database
        List<MatchQuery> matchQueryList = matchQueryRepository.findAll();
        assertThat(matchQueryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMatchQueries() throws Exception {
        // Initialize the database
        matchQueryRepository.saveAndFlush(matchQuery);

        // Get all the matchQueryList
        restMatchQueryMockMvc.perform(get("/api/match-queries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(matchQuery.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT.doubleValue())));
    }

    @Test
    @Transactional
    public void getMatchQuery() throws Exception {
        // Initialize the database
        matchQueryRepository.saveAndFlush(matchQuery);

        // Get the matchQuery
        restMatchQueryMockMvc.perform(get("/api/match-queries/{id}", matchQuery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(matchQuery.getId().intValue()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.result").value(DEFAULT_RESULT.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMatchQuery() throws Exception {
        // Get the matchQuery
        restMatchQueryMockMvc.perform(get("/api/match-queries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMatchQuery() throws Exception {
        // Initialize the database
        matchQueryRepository.saveAndFlush(matchQuery);
        int databaseSizeBeforeUpdate = matchQueryRepository.findAll().size();

        // Update the matchQuery
        MatchQuery updatedMatchQuery = matchQueryRepository.findOne(matchQuery.getId());
        // Disconnect from session so that the updates on updatedMatchQuery are not directly saved in db
        em.detach(updatedMatchQuery);
        updatedMatchQuery
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .url(UPDATED_URL)
            .result(UPDATED_RESULT);

        restMatchQueryMockMvc.perform(put("/api/match-queries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMatchQuery)))
            .andExpect(status().isOk());

        // Validate the MatchQuery in the database
        List<MatchQuery> matchQueryList = matchQueryRepository.findAll();
        assertThat(matchQueryList).hasSize(databaseSizeBeforeUpdate);
        MatchQuery testMatchQuery = matchQueryList.get(matchQueryList.size() - 1);
        assertThat(testMatchQuery.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testMatchQuery.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testMatchQuery.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testMatchQuery.getResult()).isEqualTo(UPDATED_RESULT);
    }

    @Test
    @Transactional
    public void updateNonExistingMatchQuery() throws Exception {
        int databaseSizeBeforeUpdate = matchQueryRepository.findAll().size();

        // Create the MatchQuery

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMatchQueryMockMvc.perform(put("/api/match-queries")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchQuery)))
            .andExpect(status().isCreated());

        // Validate the MatchQuery in the database
        List<MatchQuery> matchQueryList = matchQueryRepository.findAll();
        assertThat(matchQueryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMatchQuery() throws Exception {
        // Initialize the database
        matchQueryRepository.saveAndFlush(matchQuery);
        int databaseSizeBeforeDelete = matchQueryRepository.findAll().size();

        // Get the matchQuery
        restMatchQueryMockMvc.perform(delete("/api/match-queries/{id}", matchQuery.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MatchQuery> matchQueryList = matchQueryRepository.findAll();
        assertThat(matchQueryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatchQuery.class);
        MatchQuery matchQuery1 = new MatchQuery();
        matchQuery1.setId(1L);
        MatchQuery matchQuery2 = new MatchQuery();
        matchQuery2.setId(matchQuery1.getId());
        assertThat(matchQuery1).isEqualTo(matchQuery2);
        matchQuery2.setId(2L);
        assertThat(matchQuery1).isNotEqualTo(matchQuery2);
        matchQuery1.setId(null);
        assertThat(matchQuery1).isNotEqualTo(matchQuery2);
    }
}
