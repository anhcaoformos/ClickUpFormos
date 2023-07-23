package com.formos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.formos.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DownloadHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DownloadHistory.class);
        DownloadHistory downloadHistory1 = new DownloadHistory();
        downloadHistory1.setId(1L);
        DownloadHistory downloadHistory2 = new DownloadHistory();
        downloadHistory2.setId(downloadHistory1.getId());
        assertThat(downloadHistory1).isEqualTo(downloadHistory2);
        downloadHistory2.setId(2L);
        assertThat(downloadHistory1).isNotEqualTo(downloadHistory2);
        downloadHistory1.setId(null);
        assertThat(downloadHistory1).isNotEqualTo(downloadHistory2);
    }
}
