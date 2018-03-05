package net.greyferret.ferretb0t.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "timer")
public class Timer {
    private static final Logger logger = LogManager.getLogger();

    @Column(name = "name")
    private String name;
    @Column(name = "id")
    @Id
    private Long id;
    @Column(name = "time")
    private Long time;
}
