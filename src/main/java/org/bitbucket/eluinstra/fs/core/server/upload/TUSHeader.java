package org.bitbucket.eluinstra.fs.core.server.upload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum TUSHeader
{
	UPLOAD_OFFSET("Upload-Offset",null),
	UPLOAD_LENGTH("Upload-Length",null),
	TUS_VERSION("Tus-Version","1.0.0"),
	TUS_RESUMABLE("Tus-Resumable","1.0.0"),
	TUS_EXTENSION("Tus-Extension","creation"),
	TUS_MAX_SIZE("Tus-Max-Size",null),
	X_HTTP_METHOD_OVERRIDE("X-HTTP-Method-Override",null);
	
	String headerName;
	String defaultValue;
}
