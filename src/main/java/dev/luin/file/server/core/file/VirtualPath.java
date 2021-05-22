package dev.luin.file.server.core.file;

import org.apache.commons.lang3.RandomStringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.ToString.Include;
import lombok.Value;
import lombok.val;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
class VirtualPath
{
	@Include
	String value;

	static VirtualPath create(FSFileDAO fsFileDAO, int virtualPathLength)
	{
		while (true)
		{
			val result = RandomStringUtils.randomAlphanumeric(virtualPathLength);
			if (fsFileDAO.findFile(result).isEmpty())
				return new VirtualPath("/" + result.toString());
		}
	}
}
