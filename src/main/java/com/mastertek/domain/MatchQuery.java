package com.mastertek.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A MatchQuery.
 */
@Entity
@Table(name = "match_query")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MatchQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "url")
    private String url;

    @Column(name = "result")
    private Float result;

    @ManyToOne
    private Person person;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public MatchQuery image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public MatchQuery imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getUrl() {
        return url;
    }

    public MatchQuery url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Float getResult() {
        return result;
    }

    public MatchQuery result(Float result) {
        this.result = result;
        return this;
    }

    public void setResult(Float result) {
        this.result = result;
    }

    public Person getPerson() {
        return person;
    }

    public MatchQuery person(Person person) {
        this.person = person;
        return this;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MatchQuery matchQuery = (MatchQuery) o;
        if (matchQuery.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), matchQuery.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MatchQuery{" +
            "id=" + getId() +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", url='" + getUrl() + "'" +
            ", result=" + getResult() +
            "}";
    }
}
