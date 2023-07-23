package com.formos.repository;

import com.formos.domain.DownloadHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DownloadHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DownloadHistoryRepository extends JpaRepository<DownloadHistory, Long> {}
