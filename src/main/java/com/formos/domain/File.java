package com.formos.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A File.
 */
@Entity
@Table(name = "file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "file_on_server")
    private String fileOnServer;

    @Column(name = "relative_path")
    private String relativePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "files", "profile" }, allowSetters = true)
    private DownloadHistory downloadHistory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public File id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public File name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileOnServer() {
        return this.fileOnServer;
    }

    public File fileOnServer(String fileOnServer) {
        this.setFileOnServer(fileOnServer);
        return this;
    }

    public void setFileOnServer(String fileOnServer) {
        this.fileOnServer = fileOnServer;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public File relativePath(String relativePath) {
        this.setRelativePath(relativePath);
        return this;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public DownloadHistory getDownloadHistory() {
        return this.downloadHistory;
    }

    public void setDownloadHistory(DownloadHistory downloadHistory) {
        this.downloadHistory = downloadHistory;
    }

    public File downloadHistory(DownloadHistory downloadHistory) {
        this.setDownloadHistory(downloadHistory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof File)) {
            return false;
        }
        return id != null && id.equals(((File) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "File{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fileOnServer='" + getFileOnServer() + "'" +
            ", relativePath='" + getRelativePath() + "'" +
            "}";
    }
}
