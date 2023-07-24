package com.formos.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.formos.IntegrationTest;
import com.formos.domain.File;
import com.formos.repository.FileRepository;
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
 * Integration tests for the {@link FileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FileResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_ON_SERVER = "AAAAAAAAAA";
    private static final String UPDATED_FILE_ON_SERVER = "BBBBBBBBBB";

    private static final String DEFAULT_RELATIVE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_RELATIVE_PATH = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/files";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFileMockMvc;

    private File file;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static File createEntity(EntityManager em) {
        File file = new File().name(DEFAULT_NAME).fileOnServer(DEFAULT_FILE_ON_SERVER).relativePath(DEFAULT_RELATIVE_PATH);
        return file;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static File createUpdatedEntity(EntityManager em) {
        File file = new File().name(UPDATED_NAME).fileOnServer(UPDATED_FILE_ON_SERVER).relativePath(UPDATED_RELATIVE_PATH);
        return file;
    }

    @BeforeEach
    public void initTest() {
        file = createEntity(em);
    }

    @Test
    @Transactional
    void createFile() throws Exception {
        int databaseSizeBeforeCreate = fileRepository.findAll().size();
        // Create the File
        restFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(file)))
            .andExpect(status().isCreated());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeCreate + 1);
        File testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFile.getFileOnServer()).isEqualTo(DEFAULT_FILE_ON_SERVER);
        assertThat(testFile.getRelativePath()).isEqualTo(DEFAULT_RELATIVE_PATH);
    }

    @Test
    @Transactional
    void createFileWithExistingId() throws Exception {
        // Create the File with an existing ID
        file.setId(1L);

        int databaseSizeBeforeCreate = fileRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(file)))
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFiles() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        // Get all the fileList
        restFileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(file.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fileOnServer").value(hasItem(DEFAULT_FILE_ON_SERVER)))
            .andExpect(jsonPath("$.[*].relativePath").value(hasItem(DEFAULT_RELATIVE_PATH)));
    }

    @Test
    @Transactional
    void getFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        // Get the file
        restFileMockMvc
            .perform(get(ENTITY_API_URL_ID, file.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(file.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fileOnServer").value(DEFAULT_FILE_ON_SERVER))
            .andExpect(jsonPath("$.relativePath").value(DEFAULT_RELATIVE_PATH));
    }

    @Test
    @Transactional
    void getNonExistingFile() throws Exception {
        // Get the file
        restFileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file
        File updatedFile = fileRepository.findById(file.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFile are not directly saved in db
        em.detach(updatedFile);
        updatedFile.name(UPDATED_NAME).fileOnServer(UPDATED_FILE_ON_SERVER).relativePath(UPDATED_RELATIVE_PATH);

        restFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFile.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFile))
            )
            .andExpect(status().isOk());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        File testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFile.getFileOnServer()).isEqualTo(UPDATED_FILE_ON_SERVER);
        assertThat(testFile.getRelativePath()).isEqualTo(UPDATED_RELATIVE_PATH);
    }

    @Test
    @Transactional
    void putNonExistingFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        file.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, file.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(file))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        file.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(file))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        file.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(file)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFileWithPatch() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file using partial update
        File partialUpdatedFile = new File();
        partialUpdatedFile.setId(file.getId());

        partialUpdatedFile.name(UPDATED_NAME);

        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFile))
            )
            .andExpect(status().isOk());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        File testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFile.getFileOnServer()).isEqualTo(DEFAULT_FILE_ON_SERVER);
        assertThat(testFile.getRelativePath()).isEqualTo(DEFAULT_RELATIVE_PATH);
    }

    @Test
    @Transactional
    void fullUpdateFileWithPatch() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        int databaseSizeBeforeUpdate = fileRepository.findAll().size();

        // Update the file using partial update
        File partialUpdatedFile = new File();
        partialUpdatedFile.setId(file.getId());

        partialUpdatedFile.name(UPDATED_NAME).fileOnServer(UPDATED_FILE_ON_SERVER).relativePath(UPDATED_RELATIVE_PATH);

        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFile.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFile))
            )
            .andExpect(status().isOk());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
        File testFile = fileList.get(fileList.size() - 1);
        assertThat(testFile.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFile.getFileOnServer()).isEqualTo(UPDATED_FILE_ON_SERVER);
        assertThat(testFile.getRelativePath()).isEqualTo(UPDATED_RELATIVE_PATH);
    }

    @Test
    @Transactional
    void patchNonExistingFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        file.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, file.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(file))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        file.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(file))
            )
            .andExpect(status().isBadRequest());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFile() throws Exception {
        int databaseSizeBeforeUpdate = fileRepository.findAll().size();
        file.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(file)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the File in the database
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFile() throws Exception {
        // Initialize the database
        fileRepository.saveAndFlush(file);

        int databaseSizeBeforeDelete = fileRepository.findAll().size();

        // Delete the file
        restFileMockMvc
            .perform(delete(ENTITY_API_URL_ID, file.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<File> fileList = fileRepository.findAll();
        assertThat(fileList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
