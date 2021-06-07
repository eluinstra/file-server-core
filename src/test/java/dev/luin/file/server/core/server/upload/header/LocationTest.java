package dev.luin.file.server.core.server.upload.header;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.server.upload.UploadResponse;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class LocationTest
{
	@Test
	void testWrite()
	{
		val mock = Mockito.mock(UploadResponse.class);
		val location = "location";
		Location.write(mock,location);
		Mockito.verify(mock).setHeader("Location",location);
	}
}
