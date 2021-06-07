package dev.luin.file.server.core.server.upload.header;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import dev.luin.file.server.core.server.upload.UploadException;
import dev.luin.file.server.core.server.upload.UploadResponse;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class TusResumableTest
{
	@Test
	void testValidTusResumable()
	{
		assertDoesNotThrow(() -> TusResumable.validate("1.0.0"));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidTusResumable()
	{
		return Stream.of(
				null,
				"",
				"1")
				.map(input -> dynamicTest("Input: " + input,() -> assertThrows(UploadException.class,() -> TusResumable.validate(input))));
	}

	@Test
	void testWrite()
	{
		val mock = Mockito.mock(UploadResponse.class);
		TusResumable.write(mock);
		Mockito.verify(mock).setHeader("Tus-Resumable","1.0.0");
	}
}
