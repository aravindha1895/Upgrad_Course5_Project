package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@NamedQueries({
        @NamedQuery(name = "allQuestions", query = "select qt from QuestionEntity qt"),
        @NamedQuery(name = "oneQuestion", query = "select qt from QuestionEntity qt where qt.uuid = :uuid"),
        @NamedQuery(name = "editQuestion", query = "update QuestionEntity qt  set qt.content = :content Where qt.uuid = :uuid"),
        @NamedQuery(name = "deleteQuestion", query = "delete from QuestionEntity qt  Where qt.uuid = :uuid"),
        @NamedQuery(name = "allQuestionsByUserId", query = "select qt from QuestionEntity qt where qt.user.uuid = :uuid")


})

@Entity
@Table(name = "QUESTION")
public class QuestionEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @Size(max = 64)
    private String uuid;


    @Column(name = "CONTENT")
    private String content;

    @Column(name = "DATE")
    private ZonedDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(ZonedDateTime created_at) {
        this.created_at = created_at;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }




}
