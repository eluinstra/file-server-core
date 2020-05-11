package org.bitbucket.eluinstra.fs.core.server.upload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.FSProcessorException;

public abstract class BaseHandler
{
	public abstract void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, FSProcessorException;

}
