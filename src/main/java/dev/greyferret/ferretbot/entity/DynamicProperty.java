package dev.greyferret.ferretbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "dynamic_property")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicProperty implements Serializable {
	@Id
	@Column(name = "key", updatable = false, nullable = false)
	private String key;
	@Column(name = "value")
	private String value;
}
