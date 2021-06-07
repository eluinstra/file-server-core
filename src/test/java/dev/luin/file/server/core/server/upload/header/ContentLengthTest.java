package dev.luin.file.server.core.server.upload.header;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.luin.file.server.core.file.FileLength;
import dev.luin.file.server.core.server.upload.UploadException;
import io.vavr.Tuple;
import io.vavr.collection.Stream;
import lombok.val;

@TestInstance(Lifecycle.PER_CLASS)
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
				.map(v -> dynamicTest("ContentLength=" + v,() -> assertDoesNotThrow(() -> new ContentLength(v))));
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
				.map(v -> dynamicTest("ContentLength=" + v,() -> {
						val result = assertThrows(UploadException.class,() -> new ContentLength(v));
						assertInvalidContentLength(result);
				}));
	}

	private void assertInvalidContentLength(final UploadException result)
	{
		assertEquals(HttpServletResponse.SC_BAD_REQUEST,result.toHttpException().getStatusCode());
		assertNull(result.toHttpException().getMessage());
	}

	@Test
	void testValidAssertEquals()
	{
		assertDoesNotThrow(() -> new ContentLength("0").assertEquals(0));
	}

	@Test
	void testInvalidAssertEquals()
	{
		val result = assertThrows(UploadException.class,() -> new ContentLength("0").assertEquals(1));
		assertInvalidContentLength(result);
	}

	@TestFactory
	Stream<DynamicTest> testValidValidate()
	{
		return Stream.of(
				null,
				new FileLength(100L))
				.map(v -> dynamicTest("FileLength=" + v,() -> assertDoesNotThrow(() -> new ContentLength("0").validate(new UploadOffset("1"),v))));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidValidate()
	{
		return Stream.of(
				Tuple.of("100","1"),
				Tuple.of("1","100"))
				.map(v -> dynamicTest("ContentLength=" + v._1 + ", UploadOffset=" + v._2,() -> {
						val result = assertThrows(UploadException.class,() -> new ContentLength(v._1).validate(new UploadOffset(v._2),new FileLength(100L)));
						assertInvalidContentLength(result);
				}));
	}

	@Test
	void testToFileLength()
	{
		assertEquals(0,new ContentLength("0").toFileLength().getValue());
	}
}
