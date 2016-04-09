/**
 * 
 */
package com.mystic.mocker;


import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mystic.domain.Mock;
import com.mystic.mapper.Mapper;
import com.sun.net.httpserver.Headers;

/**
 * @author Leo
 *
 */

@Data
@Lazy(false)
@Scope("prototype")
@Component("mocker")
public class Mocker {
	
	@Autowired 
	private Mapper mapper;
		
	/**
	 * Get Mock From Repository or Request New Mock
	 * @param uri
	 * @param header
	 * @return String
	 */
	public Mock getMock(String uri, Headers header) {
		return mapper.findByMockKey(uri, header);
	}
}
