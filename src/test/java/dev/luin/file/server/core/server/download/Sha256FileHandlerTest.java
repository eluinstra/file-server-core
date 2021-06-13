package dev.luin.file.server.core.server.download;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.file.FSFile;
import dev.luin.file.server.core.file.Sha256Checksum;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class Sha256FileHandlerTest
{
	@TestFactory
	Stream<DynamicTest> handleOk()
	{
		return Stream.of(
				"12345678901234567890123456789012",
				"1234567890123456789012345678901234567890123456789012345678901234")
			.map(input -> dynamicTest("Input: " + input,() -> {
				val downloadRequest = Mockito.mock(DownloadRequest.class);
				val downloadResponse = Mockito.mock(DownloadResponse.class);
				val fsFile = Mockito.mock(FSFile.class);
				Mockito.when(fsFile.getSha256Checksum()).thenReturn(new Sha256Checksum(input));
				Sha256FileHandler handler = new Sha256FileHandler(fsFile);
				handler.handle(downloadRequest,downloadResponse);
				Mockito.verify(downloadResponse).setStatusOk();
				Mockito.verify(downloadResponse).setHeader("Content-Type","text/plain");
				Mockito.verify(downloadResponse).setHeader("Content-Length",String.valueOf(input.length()));
				Mockito.verify(downloadResponse).write(input);
			}));
	}
}
