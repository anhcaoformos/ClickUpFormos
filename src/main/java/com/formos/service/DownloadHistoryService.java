package com.formos.service;

import com.formos.domain.DownloadHistory;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link DownloadHistory}.
 */
public interface DownloadHistoryService {
    /**
     * Save a downloadHistory.
     *
     * @param downloadHistory the entity to save.
     * @return the persisted entity.
     */
    DownloadHistory save(DownloadHistory downloadHistory);

    /**
     * Updates a downloadHistory.
     *
     * @param downloadHistory the entity to update.
     * @return the persisted entity.
     */
    DownloadHistory update(DownloadHistory downloadHistory);

    /**
     * Partially updates a downloadHistory.
     *
     * @param downloadHistory the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DownloadHistory> partialUpdate(DownloadHistory downloadHistory);

    /**
     * Get all the downloadHistories.
     *
     * @return the list of entities.
     */
    List<DownloadHistory> findAllByCurrentUser();

    /**
     * Get the "id" downloadHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DownloadHistory> findOne(Long id);

    /**
     * Delete the "id" downloadHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the downloadHistories by profile.
     *
     * @return the list of entities by profile.
     */
    List<DownloadHistory> findAllByProfile(Long profileId);
}
