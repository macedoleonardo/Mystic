/**
 * 
 */
package com.mystic.db.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Leo
 * Mock Entity
 */

@Data
@Entity
@Table(name = "MOCK")
public class Mock {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "MOCK_ID")
	String id;
	
	@Column(name = "MOCK_KEY")
	String key;
	
	@Column(name = "MOCK_VALUE")
	String value;
	
	@Column(name = "MOCK_CONTENT_TYPE")
	String contentType;
}
