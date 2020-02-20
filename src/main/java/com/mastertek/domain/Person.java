package com.mastertek.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.mastertek.domain.enumeration.RecordStatus;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "jhi_index")
    private String index;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @Lob
    @Column(name = "afid")
    private byte[] afid;

    @Column(name = "afid_content_type")
    private String afidContentType;

    @Column(name = "jhi_insert")
    private Instant insert;

    @Column(name = "path")
    private String path;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Person name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public Person index(String index) {
        this.index = index;
        return this;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public Person status(RecordStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public byte[] getAfid() {
        return afid;
    }

    public Person afid(byte[] afid) {
        this.afid = afid;
        return this;
    }

    public void setAfid(byte[] afid) {
        this.afid = afid;
    }

    public String getAfidContentType() {
        return afidContentType;
    }

    public Person afidContentType(String afidContentType) {
        this.afidContentType = afidContentType;
        return this;
    }

    public void setAfidContentType(String afidContentType) {
        this.afidContentType = afidContentType;
    }

    public Instant getInsert() {
        return insert;
    }

    public Person insert(Instant insert) {
        this.insert = insert;
        return this;
    }

    public void setInsert(Instant insert) {
        this.insert = insert;
    }

    public String getPath() {
        return path;
    }

    public Person path(String path) {
        this.path = path;
        return this;
    }

    public void setPath(String path) {
        this.path = path;
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
        Person person = (Person) o;
        if (person.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), person.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", index='" + getIndex() + "'" +
            ", status='" + getStatus() + "'" +
            ", afid='" + getAfid() + "'" +
            ", afidContentType='" + getAfidContentType() + "'" +
            ", insert='" + getInsert() + "'" +
            ", path='" + getPath() + "'" +
            "}";
    }
}
