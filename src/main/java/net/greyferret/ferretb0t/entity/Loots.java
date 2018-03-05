package net.greyferret.ferretb0t.entity;

import com.google.gson.internal.LinkedTreeMap;
import net.greyferret.ferretb0t.config.SpringConfig;
import net.greyferret.ferretb0t.entity.json.loots.Ok;
import net.greyferret.ferretb0t.exception.LootsRunningLootsParsingException;
import net.greyferret.ferretb0t.util.FerretB0tUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Class for each "Loots", called Loots
 * <p>
 * Created by GreyFerret on 08.12.2017.
 */
@Entity
@Table(name = "loots")
public class Loots implements Serializable {
    private static final Logger logger = LogManager.getLogger();

    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "message")
    private String message;
    @Column(name = "id")
    @Id
    private String id;
    @Column(name = "date")
    private Date date;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "loots_name")
    private String lootsName;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "author", referencedColumnName = "loots_name")
    private Viewer viewer;

    private Loots() {
    }

    public Loots(Ok entry) {
        this.message = entry.getAttachments().getMessage();
        this.id = entry.getId();
        this.date = new Date();
        this.paid = false;
        this.lootsName = FerretB0tUtils.parseLootsAuthor(entry.getFrom().getAccount().getName());
    }

    /***
     * Method for advanced parsing of running Loots (that is currently showing)
     *
     * @param runningLoots
     */
    public Loots(LinkedTreeMap<String, java.lang.Object> runningLoots) throws LootsRunningLootsParsingException {
        try {
            this.id = String.valueOf(runningLoots.get("_id"));
            LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) runningLoots.get("attachments");
            this.message = String.valueOf(attachments.get("message"));
            LinkedTreeMap<String, Object> from = (LinkedTreeMap<String, Object>) runningLoots.get("from");
            LinkedTreeMap<String, Object> account = (LinkedTreeMap<String, Object>) from.get("account");
            this.lootsName = FerretB0tUtils.parseLootsAuthor((String) account.get("name"));
            this.date = new Date();
            this.paid = false;
        } catch (Exception e) {
            throw new LootsRunningLootsParsingException(e);
        }
    }

    /***
     * Method for advanced parsing of running Loots (that is currently showing)
     *
     * @param runningLoots
     */
    public Loots(LinkedTreeMap<String, java.lang.Object> runningLoots, Viewer viewer) throws LootsRunningLootsParsingException {
        this(runningLoots);
        this.viewer = viewer;
    }

    @Override
    public String toString() {
        Date dateLatest = this.date;
        String lootsName;
        String twitchName;
        if (viewer != null) {
            lootsName = viewer.getLootsNameWithCase();
            twitchName = viewer.getLootsNameWithCase();
        } else {
            lootsName = this.lootsName;
            twitchName = this.lootsName;
        }
        return "Loots(" + this.id + ") " + SpringConfig.getDateFormat().format(dateLatest) + ": L:" + lootsName + " / T:" + twitchName + ": \"" + this.message + "\"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loots loots = (Loots) o;

        return id.equals(loots.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Viewer getUser() {
        return viewer;
    }

    public void setUser(Viewer user) {
        this.viewer = user;
    }

    public String getLootsName() {
        return lootsName;
    }

    public void setLootsName(String lootsName) {
        this.lootsName = lootsName;
    }

    public String getAuthorTwitchName() {
        if (viewer != null)
            return this.viewer.getLoginWithCase();
        else
            return this.lootsName;
    }

    public String getAuthorLootsName() {
        return this.lootsName;
    }
}
