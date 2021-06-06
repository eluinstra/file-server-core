package dev.luin.file.server.core.server.upload.header;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.collection.Stream;

@TestInstance(value = Lifecycle.PER_CLASS)
public class ContentLengthTest
{
	@TestFactory
	Stream<DynamicTest> testValidContentLength()
	{
		return Stream.of(
				"0",
				"1",
				"1000000000000000000",
				"9223372036854775807")
				.map(input -> dynamicTest("Accepted: " + input,() -> assertDoesNotThrow(() -> new ContentLength(input))));
	}

	@Test
	void testEmptyContentLength()
	{
		assertThrows(NullPointerException.class,() -> new ContentLength(null));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidContentLength()
	{
		return Stream.of(
				"",
				"A",
				"10000000000000000000",
				"9223372036854775808",
				repeat("9", 4000))
				.map(input -> dynamicTest("Accepted: " + input,() -> assertThrows(UploadException.class,() -> new ContentLength(input))));
	}
}
