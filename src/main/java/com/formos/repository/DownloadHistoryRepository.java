package com.formos.repository;

import com.formos.domain.DownloadHistory;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DownloadHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DownloadHistoryRepository extends JpaRepository<DownloadHistory, Long> {
    List<DownloadHistory> findAllByProfileId(Long profileId);

    @Query(
        "select downloadHistory from DownloadHistory downloadHistory join downloadHistory.profile profile where profile.user.login = ?#{authentication.name}"
    )
    List<DownloadHistory> findAllByCurrentUser();
}
