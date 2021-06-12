package dev.luin.file.server.core.server.upload.header;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.server.upload.UploadResponse;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class TusExtensionTest
{
	@Test
	void testWrite()
	{
		val mock = Mockito.mock(UploadResponse.class);
		TusVersion.write(mock);
		Mockito.verify(mock).setHeader("Tus-Version","1.0.0");
	}
}