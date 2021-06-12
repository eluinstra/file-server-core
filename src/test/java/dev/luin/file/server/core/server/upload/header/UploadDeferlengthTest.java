package dev.luin.file.server.core.server.upload.header;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.server.upload.UploadRequest;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class UploadDeferlengthTest
{
	private static final String UPLOAD_DEFER_LENGTH = "Upload-Defer-Length";

	@Test
	void testValidIsDefined()
	{
		val mock = Mockito.mock(UploadRequest.class);
		Mockito.when(mock.getHeader(UPLOAD_DEFER_LENGTH)).thenReturn("1");
		assertTrue(UploadDeferLength.isDefined(mock));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidIsDefined()
	{
		val mock = Mockito.mock(UploadRequest.class);
		return Stream.of(
				null,
				"0",
				"10")
				.map(v -> dynamicTest("UploadDeferLength=" + v,() -> {
						Mockito.when(mock.getHeader(UPLOAD_DEFER_LENGTH)).thenReturn(v);
						assertFalse(UploadDeferLength.isDefined(mock));
				}));
	}
}
