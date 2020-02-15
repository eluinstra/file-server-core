package org.bitbucket.eluinstra.fs.core.file;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class SecurityManager
{
	@NonNull
	FSFileDAO fsDAO;

	public boolean isAuthorized(@NonNull final byte[] clientCertificate, @NonNull final FSFile file)
	{
		return fsDAO.isAuthorized(clientCertificate,file.getVirtualPath());
	}
}
