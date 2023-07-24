package com.formos.repository;

import com.formos.domain.File;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the File entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileRepository extends JpaRepository<File, Long> {}
