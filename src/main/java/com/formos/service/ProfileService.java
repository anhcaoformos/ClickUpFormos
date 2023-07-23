package com.formos.service;

import com.formos.domain.Profile;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Profile}.
 */
public interface ProfileService {
    /**
     * Save a profile.
     *
     * @param profile the entity to save.
     * @return the persisted entity.
     */
    Profile save(Profile profile);

    /**
     * Updates a profile.
     *
     * @param profile the entity to update.
     * @return the persisted entity.
     */
    Profile update(Profile profile);

    /**
     * Partially updates a profile.
     *
     * @param profile the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Profile> partialUpdate(Profile profile);

    /**
     * Get all the profiles.
     *
     * @return the list of entities.
     */
    List<Profile> findAll();

    /**
     * Get the "id" profile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Profile> findOne(Long id);

    /**
     * Get profiles by current user.
     *
     * @return the list of entities.
     */
    List<Profile> findAllByCurrentUser();

    /**
     * Delete the "id" profile.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
