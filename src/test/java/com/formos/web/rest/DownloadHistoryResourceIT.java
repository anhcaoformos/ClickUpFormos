package com.formos.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.formos.IntegrationTest;
import com.formos.domain.DownloadHistory;
import com.formos.repository.DownloadHistoryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DownloadHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DownloadHistoryResourceIT {

    private static final String DEFAULT_TASK_ID = "AAAAAAAAAA";
    private static final String UPDATED_TASK_ID = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_ID = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/download-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DownloadHistoryRepository downloadHistoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDownloadHistoryMockMvc;

    private DownloadHistory downloadHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DownloadHistory createEntity(EntityManager em) {
        DownloadHistory downloadHistory = new DownloadHistory().taskId(DEFAULT_TASK_ID).historyId(DEFAULT_HISTORY_ID);
        return downloadHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DownloadHistory createUpdatedEntity(EntityManager em) {
        DownloadHistory downloadHistory = new DownloadHistory().taskId(UPDATED_TASK_ID).historyId(UPDATED_HISTORY_ID);
        return downloadHistory;
    }

    @BeforeEach
    public void initTest() {
        downloadHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createDownloadHistory() throws Exception {
        int databaseSizeBeforeCreate = downloadHistoryRepository.findAll().size();
        // Create the DownloadHistory
        restDownloadHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isCreated());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        DownloadHistory testDownloadHistory = downloadHistoryList.get(downloadHistoryList.size() - 1);
        assertThat(testDownloadHistory.getTaskId()).isEqualTo(DEFAULT_TASK_ID);
        assertThat(testDownloadHistory.getHistoryId()).isEqualTo(DEFAULT_HISTORY_ID);
    }

    @Test
    @Transactional
    void createDownloadHistoryWithExistingId() throws Exception {
        // Create the DownloadHistory with an existing ID
        downloadHistory.setId(1L);

        int databaseSizeBeforeCreate = downloadHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDownloadHistoryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDownloadHistories() throws Exception {
        // Initialize the database
        downloadHistoryRepository.saveAndFlush(downloadHistory);

        // Get all the downloadHistoryList
        restDownloadHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(downloadHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskId").value(hasItem(DEFAULT_TASK_ID)))
            .andExpect(jsonPath("$.[*].historyId").value(hasItem(DEFAULT_HISTORY_ID)));
    }

    @Test
    @Transactional
    void getDownloadHistory() throws Exception {
        // Initialize the database
        downloadHistoryRepository.saveAndFlush(downloadHistory);

        // Get the downloadHistory
        restDownloadHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, downloadHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(downloadHistory.getId().intValue()))
            .andExpect(jsonPath("$.taskId").value(DEFAULT_TASK_ID))
            .andExpect(jsonPath("$.historyId").value(DEFAULT_HISTORY_ID));
    }

    @Test
    @Transactional
    void getNonExistingDownloadHistory() throws Exception {
        // Get the downloadHistory
        restDownloadHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDownloadHistory() throws Exception {
        // Initialize the database
        downloadHistoryRepository.saveAndFlush(downloadHistory);

        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();

        // Update the downloadHistory
        DownloadHistory updatedDownloadHistory = downloadHistoryRepository.findById(downloadHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDownloadHistory are not directly saved in db
        em.detach(updatedDownloadHistory);
        updatedDownloadHistory.taskId(UPDATED_TASK_ID).historyId(UPDATED_HISTORY_ID);

        restDownloadHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDownloadHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDownloadHistory))
            )
            .andExpect(status().isOk());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
        DownloadHistory testDownloadHistory = downloadHistoryList.get(downloadHistoryList.size() - 1);
        assertThat(testDownloadHistory.getTaskId()).isEqualTo(UPDATED_TASK_ID);
        assertThat(testDownloadHistory.getHistoryId()).isEqualTo(UPDATED_HISTORY_ID);
    }

    @Test
    @Transactional
    void putNonExistingDownloadHistory() throws Exception {
        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();
        downloadHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDownloadHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, downloadHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDownloadHistory() throws Exception {
        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();
        downloadHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDownloadHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDownloadHistory() throws Exception {
        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();
        downloadHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDownloadHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDownloadHistoryWithPatch() throws Exception {
        // Initialize the database
        downloadHistoryRepository.saveAndFlush(downloadHistory);

        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();

        // Update the downloadHistory using partial update
        DownloadHistory partialUpdatedDownloadHistory = new DownloadHistory();
        partialUpdatedDownloadHistory.setId(downloadHistory.getId());

        restDownloadHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDownloadHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDownloadHistory))
            )
            .andExpect(status().isOk());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
        DownloadHistory testDownloadHistory = downloadHistoryList.get(downloadHistoryList.size() - 1);
        assertThat(testDownloadHistory.getTaskId()).isEqualTo(DEFAULT_TASK_ID);
        assertThat(testDownloadHistory.getHistoryId()).isEqualTo(DEFAULT_HISTORY_ID);
    }

    @Test
    @Transactional
    void fullUpdateDownloadHistoryWithPatch() throws Exception {
        // Initialize the database
        downloadHistoryRepository.saveAndFlush(downloadHistory);

        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();

        // Update the downloadHistory using partial update
        DownloadHistory partialUpdatedDownloadHistory = new DownloadHistory();
        partialUpdatedDownloadHistory.setId(downloadHistory.getId());

        partialUpdatedDownloadHistory.taskId(UPDATED_TASK_ID).historyId(UPDATED_HISTORY_ID);

        restDownloadHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDownloadHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDownloadHistory))
            )
            .andExpect(status().isOk());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
        DownloadHistory testDownloadHistory = downloadHistoryList.get(downloadHistoryList.size() - 1);
        assertThat(testDownloadHistory.getTaskId()).isEqualTo(UPDATED_TASK_ID);
        assertThat(testDownloadHistory.getHistoryId()).isEqualTo(UPDATED_HISTORY_ID);
    }

    @Test
    @Transactional
    void patchNonExistingDownloadHistory() throws Exception {
        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();
        downloadHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDownloadHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, downloadHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDownloadHistory() throws Exception {
        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();
        downloadHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDownloadHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDownloadHistory() throws Exception {
        int databaseSizeBeforeUpdate = downloadHistoryRepository.findAll().size();
        downloadHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDownloadHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(downloadHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DownloadHistory in the database
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDownloadHistory() throws Exception {
        // Initialize the database
        downloadHistoryRepository.saveAndFlush(downloadHistory);

        int databaseSizeBeforeDelete = downloadHistoryRepository.findAll().size();

        // Delete the downloadHistory
        restDownloadHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, downloadHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DownloadHistory> downloadHistoryList = downloadHistoryRepository.findAll();
        assertThat(downloadHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
