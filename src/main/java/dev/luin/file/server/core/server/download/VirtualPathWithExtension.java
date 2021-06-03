package dev.luin.file.server.core.server.download;

import dev.luin.file.server.core.ValueObject;
import dev.luin.file.server.core.file.VirtualPath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VirtualPathWithExtension implements ValueObject<VirtualPath>
{
	VirtualPath value;
	Extension extension;

	public VirtualPathWithExtension(String path)
	{
		extension = Extension.create(path);
		value = new VirtualPath(extension.getPath(path));
	}
}
