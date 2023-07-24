package com.formos.service.impl;

import com.formos.domain.File;
import com.formos.repository.FileRepository;
import com.formos.service.FileService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link File}.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File save(File file) {
        log.debug("Request to save File : {}", file);
        return fileRepository.save(file);
    }

    @Override
    public File update(File file) {
        log.debug("Request to update File : {}", file);
        return fileRepository.save(file);
    }

    @Override
    public Optional<File> partialUpdate(File file) {
        log.debug("Request to partially update File : {}", file);

        return fileRepository
            .findById(file.getId())
            .map(existingFile -> {
                if (file.getName() != null) {
                    existingFile.setName(file.getName());
                }
                if (file.getFileOnServer() != null) {
                    existingFile.setFileOnServer(file.getFileOnServer());
                }
                if (file.getRelativePath() != null) {
                    existingFile.setRelativePath(file.getRelativePath());
                }

                return existingFile;
            })
            .map(fileRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> findAll() {
        log.debug("Request to get all Files");
        return fileRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<File> findOne(Long id) {
        log.debug("Request to get File : {}", id);
        return fileRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete File : {}", id);
        fileRepository.deleteById(id);
    }
}
