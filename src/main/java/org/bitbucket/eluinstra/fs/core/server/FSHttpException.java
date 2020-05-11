package org.bitbucket.eluinstra.fs.core.server;

import java.util.Collections;
import java.util.Map;

import javax.xml.ws.http.HTTPException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class FSHttpException extends HTTPException
{
	private static final long serialVersionUID = 1L;
	String message;
	Map<String,String> headers;

	public FSHttpException(int statusCode)
	{
		this(statusCode,null,Collections.emptyMap());
	}

	public FSHttpException(int statusCode, String message)
	{
		this(statusCode,message,Collections.emptyMap());
	}

	public FSHttpException(int statusCode, Map<String,String> headers)
	{
		this(statusCode,null,headers);
	}

	public FSHttpException(int statusCode, String message, Map<String,String> headers)
	{
		super(statusCode);
		this.message = message;
		this.headers = headers;
	}
}
