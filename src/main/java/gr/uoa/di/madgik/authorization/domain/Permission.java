package gr.uoa.di.madgik.authorization.domain;

import jakarta.persistence.*;

@Entity
public class Permission {
    private static final String DEFAULT_GROUP = "default";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String subjectGroup = DEFAULT_GROUP;

    private String subject;
    private String action;
    private String object;

    public Permission() {}

    public Permission(String subject, String action, String object) {
        this.subject = subject;
        this.action = action;
        this.object = object;
    }

    public Permission(String subject, String action, String object, String subjectGroup) {
        this.subject = subject;
        this.action = action;
        this.object = object;
        this.subjectGroup = subjectGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String group) {
        this.subjectGroup = group;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", subjectGroup='" + subjectGroup + '\'' +
                ", subject='" + subject + '\'' +
                ", action='" + action + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
