package com.formos.web.rest;

import com.formos.domain.Profile;
import com.formos.repository.ProfileRepository;
import com.formos.service.ClickUpService;
import com.formos.service.ProfileService;
import com.formos.service.dto.clickup.ProjectDTO;
import com.formos.service.dto.clickup.SubCategoryDTO;
import com.formos.service.dto.clickup.TeamDTO;
import com.formos.web.rest.errors.BadRequestAlertException;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.formos.domain.Profile}.
 */
@RestController
@RequestMapping("/api")
public class ProfileResource {

    private final Logger log = LoggerFactory.getLogger(ProfileResource.class);

    private static final String ENTITY_NAME = "profile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfileService profileService;

    private final ProfileRepository profileRepository;

    private final ClickUpService clickUpService;

    public ProfileResource(ProfileService profileService, ProfileRepository profileRepository, ClickUpService clickUpService) {
        this.profileService = profileService;
        this.profileRepository = profileRepository;
        this.clickUpService = clickUpService;
    }

    /**
     * {@code POST  /profiles} : Create a new profile.
     *
     * @param profile the profile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profile, or with status {@code 400 (Bad Request)} if the profile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/profiles")
    public ResponseEntity<Profile> createProfile(@Valid @RequestBody Profile profile) throws URISyntaxException {
        log.debug("REST request to save Profile : {}", profile);
        if (profile.getId() != null) {
            throw new BadRequestAlertException("A new profile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Profile result = profileService.save(profile);
        return ResponseEntity
            .created(new URI("/api/profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /profiles/:id} : Updates an existing profile.
     *
     * @param id the id of the profile to save.
     * @param profile the profile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profile,
     * or with status {@code 400 (Bad Request)} if the profile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/profiles/{id}")
    public ResponseEntity<Profile> updateProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Profile profile
    ) throws URISyntaxException {
        log.debug("REST request to update Profile : {}, {}", id, profile);
        if (profile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Profile result = profileService.update(profile);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, profile.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /profiles/:id} : Partial updates given fields of an existing profile, field will ignore if it is null
     *
     * @param id the id of the profile to save.
     * @param profile the profile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profile,
     * or with status {@code 400 (Bad Request)} if the profile is not valid,
     * or with status {@code 404 (Not Found)} if the profile is not found,
     * or with status {@code 500 (Internal Server Error)} if the profile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/profiles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Profile> partialUpdateProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Profile profile
    ) throws URISyntaxException {
        log.debug("REST request to partial update Profile partially : {}, {}", id, profile);
        if (profile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Profile> result = profileService.partialUpdate(profile);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, profile.getId().toString())
        );
    }

    /**
     * {@code GET  /profiles} : get all the profiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of profiles in body.
     */
    //    @GetMapping("/profiles")
    //    public List<Profile> getAllProfiles() {
    //        log.debug("REST request to get all Profiles");
    //        return profileService.findAll();
    //    }

    /**
     * {@code GET  /profiles/:id} : get the "id" profile.
     *
     * @param id the id of the profile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/profiles/{id}")
    public ResponseEntity<Profile> getProfile(@PathVariable Long id) {
        log.debug("REST request to get Profile : {}", id);
        Optional<Profile> profile = profileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profile);
    }

    /**
     * {@code DELETE  /profiles/:id} : delete the "id" profile.
     *
     * @param id the id of the profile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/profiles/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        log.debug("REST request to delete Profile : {}", id);
        profileService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /profiles} : get the profiles by current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/profiles")
    public List<Profile> getProfilesByCurrentUser() {
        log.debug("REST request to get Profiles by current user");
        return profileService.findAllByCurrentUser();
    }

    @PostMapping("/profiles/{id}/generate-tasks")
    public void exportClickUp(@PathVariable Long id, @RequestBody List<String> taskIds, HttpServletResponse response) throws Exception {
        ZipFile zipFile = clickUpService.exportPdfForTasks(id, taskIds);
        String header = String.format("attachment; filename=\"%s\"", zipFile.getFile().getName());
        response.setHeader("Content-Disposition", header);
        Path path = Paths.get(zipFile.getFile().getPath());
        response.reset();
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String mimeType = mimeTypesMap.getContentType(zipFile.getFile().getName());
        response.setContentType(mimeType);
        byte[] data = Files.exists(path) ? Files.readAllBytes(path) : new byte[1];
        response.setContentLength(data.length);
        response.setHeader("Content-Disposition", header);
        response.setStatus(HttpServletResponse.SC_OK);
        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(new ByteArrayInputStream(data), outputStream);
        outputStream.flush();
    }

    @GetMapping("/profiles/{id}/team")
    public List<TeamDTO> getTeams(@PathVariable Long id) throws Exception {
        log.debug("REST request to get Profile : {}", id);
        return clickUpService.getTeams(id);
    }

    @GetMapping("/profiles/{id}/subcategory/{subCategoryId}/tasks")
    public List<String> getTaskIds(@PathVariable Long id, @PathVariable String subCategoryId) throws Exception {
        log.debug("REST request to get Profile : {}", id);
        return clickUpService.getTaskIds(id, subCategoryId);
    }
}
