package gr.athenarc.authorization;

import javax.persistence.*;

@Entity
public class AuthTriple {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String subject;
    private String action;
    private String object;

    public AuthTriple() {}

    public AuthTriple(String subject, String action, String object) {
        this.subject = subject;
        this.action = action;
        this.object = object;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
