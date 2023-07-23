package com.formos.service.impl;

import com.formos.domain.Profile;
import com.formos.domain.User;
import com.formos.repository.ProfileRepository;
import com.formos.service.ProfileService;
import com.formos.service.UserService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Profile}.
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    @Override
    public Profile save(Profile profile) {
        if (Objects.isNull(profile.getUser())) {
            userService.getUserWithAuthorities().ifPresent(profile::setUser);
        }
        log.debug("Request to save Profile : {}", profile);
        return profileRepository.save(profile);
    }

    @Override
    public Profile update(Profile profile) {
        if (Objects.isNull(profile.getUser())) {
            userService.getUserWithAuthorities().ifPresent(profile::setUser);
        }
        log.debug("Request to update Profile : {}", profile);
        return profileRepository.save(profile);
    }

    @Override
    public Optional<Profile> partialUpdate(Profile profile) {
        log.debug("Request to partially update Profile : {}", profile);

        return profileRepository
            .findById(profile.getId())
            .map(existingProfile -> {
                if (profile.getName() != null) {
                    existingProfile.setName(profile.getName());
                }
                if (profile.getUsername() != null) {
                    existingProfile.setUsername(profile.getUsername());
                }
                if (profile.getPassword() != null) {
                    existingProfile.setPassword(profile.getPassword());
                }
                if (profile.getApiKey() != null) {
                    existingProfile.setApiKey(profile.getApiKey());
                }
                if (profile.getToken() != null) {
                    existingProfile.setToken(profile.getToken());
                }
                if (profile.getBaseUrl() != null) {
                    existingProfile.setBaseUrl(profile.getBaseUrl());
                }

                return existingProfile;
            })
            .map(profileRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> findAll() {
        log.debug("Request to get all Profiles");
        return profileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findOne(Long id) {
        log.debug("Request to get Profile : {}", id);
        return profileRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Profile> findAllByCurrentUser() {
        log.debug("Request to get all Profiles by current user");
        return profileRepository.findByUserIsCurrentUser();
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Profile : {}", id);
        profileRepository.deleteById(id);
    }
}
