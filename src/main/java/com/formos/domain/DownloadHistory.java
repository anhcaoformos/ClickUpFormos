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

    @Column(name = "timestamp")
    private String timestamp;

    @Lob
    @Column(name = "task_data")
    private String taskData;

    @Lob
    @Column(name = "histories_data")
    private String historiesData;

    @Lob
    @Column(name = "children_comment_data")
    private String childrenCommentData;

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

    public String getTimestamp() {
        return this.timestamp;
    }

    public DownloadHistory timestamp(String timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTaskData() {
        return this.taskData;
    }

    public DownloadHistory taskData(String taskData) {
        this.setTaskData(taskData);
        return this;
    }

    public void setTaskData(String taskData) {
        this.taskData = taskData;
    }

    public String getHistoriesData() {
        return this.historiesData;
    }

    public DownloadHistory historiesData(String historiesData) {
        this.setHistoriesData(historiesData);
        return this;
    }

    public void setHistoriesData(String historiesData) {
        this.historiesData = historiesData;
    }

    public String getChildrenCommentData() {
        return childrenCommentData;
    }

    public void setChildrenCommentData(String childrenCommentData) {
        this.childrenCommentData = childrenCommentData;
    }

    public DownloadHistory childrenCommentData(String childrenCommentData) {
        this.setChildrenCommentData(childrenCommentData);
        return this;
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
            ", timestamp='" + getTimestamp() + "'" +
            ", taskData='" + getTaskData() + "'" +
            ", historiesData='" + getHistoriesData() + "'" +
            "}";
    }
}
