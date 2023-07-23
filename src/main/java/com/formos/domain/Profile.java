package com.formos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Profile.
 */
@Entity
@Table(name = "profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "token")
    @JsonIgnore
    private String token;

    @NotNull
    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "profile" }, allowSetters = true)
    private Set<DownloadHistory> downloadHistories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "profiles" }, allowSetters = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Profile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Profile name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public Profile username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public Profile password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public Profile apiKey(String apiKey) {
        this.setApiKey(apiKey);
        return this;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getToken() {
        return this.token;
    }

    public Profile token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public Profile baseUrl(String baseUrl) {
        this.setBaseUrl(baseUrl);
        return this;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Set<DownloadHistory> getDownloadHistories() {
        return this.downloadHistories;
    }

    public void setDownloadHistories(Set<DownloadHistory> downloadHistories) {
        if (this.downloadHistories != null) {
            this.downloadHistories.forEach(i -> i.setProfile(null));
        }
        if (downloadHistories != null) {
            downloadHistories.forEach(i -> i.setProfile(this));
        }
        this.downloadHistories = downloadHistories;
    }

    public Profile downloadHistories(Set<DownloadHistory> downloadHistories) {
        this.setDownloadHistories(downloadHistories);
        return this;
    }

    public Profile addDownloadHistory(DownloadHistory downloadHistory) {
        this.downloadHistories.add(downloadHistory);
        downloadHistory.setProfile(this);
        return this;
    }

    public Profile removeDownloadHistory(DownloadHistory downloadHistory) {
        this.downloadHistories.remove(downloadHistory);
        downloadHistory.setProfile(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Profile user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        return id != null && id.equals(((Profile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profile{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", apiKey='" + getApiKey() + "'" +
            ", token='" + getToken() + "'" +
            ", baseUrl='" + getBaseUrl() + "'" +
            "}";
    }
}
