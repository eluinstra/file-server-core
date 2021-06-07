package dev.luin.file.server.core.server.upload.header;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.server.upload.UploadException;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
public class ContentTypeTest
{
	@Test
	void testValidContentType()
	{
		assertDoesNotThrow(() -> ContentType.validate("application/offset+octet-stream"));
	}

	@Test
	void testEmptyContentType()
	{
		val result = assertThrows(UploadException.class, () -> ContentType.validate((String)null));
		assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,result.toHttpException().getStatusCode()); 
	}

	@TestFactory
	Stream<DynamicTest> testInvalidContentType()
	{
		return Stream.of(
				"",
				"text/xml")
				.map(input -> dynamicTest("Input: " + input,() -> assertThrows(UploadException.class,() -> ContentType.validate(input))));
	}
}
