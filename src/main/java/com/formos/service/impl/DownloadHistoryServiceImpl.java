package com.formos.service.impl;

import com.formos.domain.DownloadHistory;
import com.formos.repository.DownloadHistoryRepository;
import com.formos.service.DownloadHistoryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DownloadHistory}.
 */
@Service
@Transactional
public class DownloadHistoryServiceImpl implements DownloadHistoryService {

    private final Logger log = LoggerFactory.getLogger(DownloadHistoryServiceImpl.class);

    private final DownloadHistoryRepository downloadHistoryRepository;

    public DownloadHistoryServiceImpl(DownloadHistoryRepository downloadHistoryRepository) {
        this.downloadHistoryRepository = downloadHistoryRepository;
    }

    @Override
    public DownloadHistory save(DownloadHistory downloadHistory) {
        log.debug("Request to save DownloadHistory : {}", downloadHistory);
        return downloadHistoryRepository.save(downloadHistory);
    }

    @Override
    public DownloadHistory update(DownloadHistory downloadHistory) {
        log.debug("Request to update DownloadHistory : {}", downloadHistory);
        return downloadHistoryRepository.save(downloadHistory);
    }

    @Override
    public Optional<DownloadHistory> partialUpdate(DownloadHistory downloadHistory) {
        log.debug("Request to partially update DownloadHistory : {}", downloadHistory);

        return downloadHistoryRepository
            .findById(downloadHistory.getId())
            .map(existingDownloadHistory -> {
                if (downloadHistory.getTaskId() != null) {
                    existingDownloadHistory.setTaskId(downloadHistory.getTaskId());
                }
                if (downloadHistory.getHistoryId() != null) {
                    existingDownloadHistory.setHistoryId(downloadHistory.getHistoryId());
                }

                return existingDownloadHistory;
            })
            .map(downloadHistoryRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DownloadHistory> findAll() {
        log.debug("Request to get all DownloadHistories");
        return downloadHistoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DownloadHistory> findOne(Long id) {
        log.debug("Request to get DownloadHistory : {}", id);
        return downloadHistoryRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DownloadHistory : {}", id);
        downloadHistoryRepository.deleteById(id);
    }
}
