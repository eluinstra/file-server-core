package org.bitbucket.eluinstra.fs.core.server.download;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bitbucket.eluinstra.fs.core.file.FileSystem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
@AllArgsConstructor
@Getter(value = AccessLevel.PACKAGE)
public abstract class BaseHandler
{
	@NonNull
	FileSystem fs;

	public abstract void handle(HttpServletRequest request, HttpServletResponse response, @NonNull byte[] clientCertificate) throws IOException;
}
