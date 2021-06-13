package dev.luin.file.server.core.server.download;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Md5Checksum;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class Md5FileHandlerTest
{
	private static final String CHECKSUM = "12345678901234567890123456789012";

	@Test
	void handleOk()
	{
		val downloadRequest = Mockito.mock(DownloadRequest.class);
		val downloadResponse = Mockito.mock(DownloadResponse.class);
		val fsFile = Mockito.mock(FSFile.class);
		Mockito.when(fsFile.getMd5Checksum()).thenReturn(new Md5Checksum(CHECKSUM));
		Md5FileHandler handler = new Md5FileHandler(fsFile);
		handler.handle(downloadRequest,downloadResponse);
		Mockito.verify(downloadResponse).setStatusOk();
		Mockito.verify(downloadResponse).setHeader("Content-Type","text/plain");
		Mockito.verify(downloadResponse).setHeader("Content-Length",String.valueOf(CHECKSUM.length()));
		Mockito.verify(downloadResponse).write(CHECKSUM);
	}
}
