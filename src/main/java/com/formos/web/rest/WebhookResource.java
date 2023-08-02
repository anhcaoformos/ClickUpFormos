package com.formos.web.rest;

import com.formos.domain.DownloadHistory;
import com.formos.repository.DownloadHistoryRepository;
import com.formos.service.DownloadHistoryService;
import com.formos.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.formos.domain.DownloadHistory}.
 */
@RestController
@RequestMapping("/api")
public class DownloadHistoryResource {

    private final Logger log = LoggerFactory.getLogger(DownloadHistoryResource.class);

    private static final String ENTITY_NAME = "downloadHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DownloadHistoryService downloadHistoryService;

    private final DownloadHistoryRepository downloadHistoryRepository;

    public DownloadHistoryResource(DownloadHistoryService downloadHistoryService, DownloadHistoryRepository downloadHistoryRepository) {
        this.downloadHistoryService = downloadHistoryService;
        this.downloadHistoryRepository = downloadHistoryRepository;
    }

    /**
     * {@code POST  /download-histories} : Create a new downloadHistory.
     *
     * @param downloadHistory the downloadHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new downloadHistory, or with status {@code 400 (Bad Request)} if the downloadHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/download-histories")
    public ResponseEntity<DownloadHistory> createDownloadHistory(@RequestBody DownloadHistory downloadHistory) throws URISyntaxException {
        log.debug("REST request to save DownloadHistory : {}", downloadHistory);
        if (downloadHistory.getId() != null) {
            throw new BadRequestAlertException("A new downloadHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DownloadHistory result = downloadHistoryService.save(downloadHistory);
        return ResponseEntity
            .created(new URI("/api/download-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /download-histories/:id} : Updates an existing downloadHistory.
     *
     * @param id the id of the downloadHistory to save.
     * @param downloadHistory the downloadHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated downloadHistory,
     * or with status {@code 400 (Bad Request)} if the downloadHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the downloadHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/download-histories/{id}")
    public ResponseEntity<DownloadHistory> updateDownloadHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DownloadHistory downloadHistory
    ) throws URISyntaxException {
        log.debug("REST request to update DownloadHistory : {}, {}", id, downloadHistory);
        if (downloadHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, downloadHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!downloadHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DownloadHistory result = downloadHistoryService.update(downloadHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, downloadHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /download-histories/:id} : Partial updates given fields of an existing downloadHistory, field will ignore if it is null
     *
     * @param id the id of the downloadHistory to save.
     * @param downloadHistory the downloadHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated downloadHistory,
     * or with status {@code 400 (Bad Request)} if the downloadHistory is not valid,
     * or with status {@code 404 (Not Found)} if the downloadHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the downloadHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/download-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DownloadHistory> partialUpdateDownloadHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DownloadHistory downloadHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update DownloadHistory partially : {}, {}", id, downloadHistory);
        if (downloadHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, downloadHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!downloadHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DownloadHistory> result = downloadHistoryService.partialUpdate(downloadHistory);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, downloadHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /download-histories} : get all the downloadHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of downloadHistories in body.
     */
    @GetMapping("/download-histories")
    public List<DownloadHistory> getAllDownloadHistoriesByCurrentUser() {
        log.debug("REST request to get all DownloadHistories by current user");
        return downloadHistoryService.findAllByCurrentUser();
    }

    /**
     * {@code GET  /download-histories/:id} : get the "id" downloadHistory.
     *
     * @param id the id of the downloadHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the downloadHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/download-histories/{id}")
    public ResponseEntity<DownloadHistory> getDownloadHistory(@PathVariable Long id) {
        log.debug("REST request to get DownloadHistory : {}", id);
        Optional<DownloadHistory> downloadHistory = downloadHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(downloadHistory);
    }

    /**
     * {@code DELETE  /download-histories/:id} : delete the "id" downloadHistory.
     *
     * @param id the id of the downloadHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/download-histories/{id}")
    public ResponseEntity<Void> deleteDownloadHistory(@PathVariable Long id) {
        log.debug("REST request to delete DownloadHistory : {}", id);
        downloadHistoryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/download-histories/profile/{profileId}")
    public List<DownloadHistory> getDownloadHistoryByProfile(@PathVariable Long profileId) {
        log.debug("REST request to get DownloadHistory by profile id: {}", profileId);
        return downloadHistoryService.findAllByProfile(profileId);
    }
}
