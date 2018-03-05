package net.greyferret.ferretb0t.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "command")
public class Command {
    private static final Logger logger = LogManager.getLogger();

    private static final String separator = ";";

    public enum MentionType {
        WITH_MENTION, WITHOUT_MENTION, CONTEXT_RELATED
    }

    @Id
    @Column(name = "code")
    private String code;
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "alternative_codes")
    private String alternativeCodes;
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "response")
    private String response;
    @Column(name = "game")
    private String game;
    @Column(name = "personal")
    private Boolean personal;
    @Column(name = "mention_type")//1: with @, 2: without @, 0 or anything else: context defined
    private Integer mentionType;

    public Command() {
        this.personal = false;
    }

    @PrePersist
    public void prePersist() {
        if (this.isPersonal() == null) {
            setPersonal(false);
        }
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "timer", referencedColumnName = "id")
    private Timer timer;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getAlternativeCodes() {
        return Arrays.asList(StringUtils.split(this.alternativeCodes, separator));
    }

    public void setAlternativeCodes(List<String> alternativeCodesList) {
        this.alternativeCodes = StringUtils.join(alternativeCodesList, separator);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Boolean isPersonal() {
        if (personal == null)
            return false;
        return personal;
    }

    public void setPersonal(Boolean personal) {
        if (personal == null)
            this.personal = false;
        this.personal = personal;
    }

    @Deprecated
    public Boolean getPersonal() {
        return isPersonal();
    }

    public List<String> getAllCodes() {
        List<String> res = new ArrayList<>();
        res.add(code);
        return res;
    }

    public MentionType getMentionType() {
        if (this.mentionType == null)
            return MentionType.CONTEXT_RELATED;
        if (mentionType == 1)
            return MentionType.WITH_MENTION;
        else if (mentionType == 2)
            return MentionType.WITHOUT_MENTION;
        else
            return MentionType.CONTEXT_RELATED;
    }

    public void setMentionType(Integer mentionType) {
        this.mentionType = mentionType;
    }

    public void setMentionType(MentionType mentionType) {
        if (mentionType == MentionType.WITH_MENTION)
            this.setMentionType(1);
        else if (mentionType == MentionType.WITHOUT_MENTION)
            this.setMentionType(2);
        else
            this.setMentionType(0);
    }
}
