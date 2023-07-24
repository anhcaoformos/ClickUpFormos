package com.formos.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DownloadHistory.
 */
@Entity
@Table(name = "download_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DownloadHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "history_id")
    private String historyId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "downloadHistory")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "downloadHistory" }, allowSetters = true)
    private Set<File> files = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "downloadHistories", "user" }, allowSetters = true)
    private Profile profile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DownloadHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public DownloadHistory taskId(String taskId) {
        this.setTaskId(taskId);
        return this;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getHistoryId() {
        return this.historyId;
    }

    public DownloadHistory historyId(String historyId) {
        this.setHistoryId(historyId);
        return this;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public Set<File> getFiles() {
        return this.files;
    }

    public void setFiles(Set<File> files) {
        if (this.files != null) {
            this.files.forEach(i -> i.setDownloadHistory(null));
        }
        if (files != null) {
            files.forEach(i -> i.setDownloadHistory(this));
        }
        this.files = files;
    }

    public DownloadHistory files(Set<File> files) {
        this.setFiles(files);
        return this;
    }

    public DownloadHistory addFile(File file) {
        this.files.add(file);
        file.setDownloadHistory(this);
        return this;
    }

    public DownloadHistory removeFile(File file) {
        this.files.remove(file);
        file.setDownloadHistory(null);
        return this;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public DownloadHistory profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DownloadHistory)) {
            return false;
        }
        return id != null && id.equals(((DownloadHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DownloadHistory{" +
            "id=" + getId() +
            ", taskId='" + getTaskId() + "'" +
            ", historyId='" + getHistoryId() + "'" +
            "}";
    }
}
