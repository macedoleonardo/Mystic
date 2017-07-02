/**
 * 
 */
package com.mystic.domain;

import com.sun.net.httpserver.Headers;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Leo
 * Mock Entity
 */

@Data
@Scope("prototype")
@Component
public class Mock {
	String uri;
	String body;
	Headers header;
	HttpStatus statusCode;
}
