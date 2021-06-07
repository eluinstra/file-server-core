package dev.luin.file.server.core.server.upload.header;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.vavr.collection.Stream;

@TestInstance(Lifecycle.PER_CLASS)
public class TusMaxSizeTest
{
	@TestFactory
	Stream<DynamicTest> testValidTusMaxSize()
	{
		return Stream.of(
				1L,
				1000000000000000000L,
				9223372036854775807L)
				.map(input -> dynamicTest("Input: " + input,() -> assertDoesNotThrow(() -> TusMaxSize.of(input))));
	}

	@TestFactory
	Stream<DynamicTest> testEmptyTusMaxSize()
	{
		return Stream.of(
				null,
				0L)
				.map(input -> dynamicTest("Input: " + input,() -> assertDoesNotThrow(() -> TusMaxSize.of(input))));
	}

	@TestFactory
	Stream<DynamicTest> testInvalidTusMaxSize()
	{
		return Stream.of(
				-1L,
				-1000000000000000000L,
				-9223372036854775808L)
				.map(input -> dynamicTest("Input: " + input,() -> assertThrows(IllegalArgumentException.class,() -> TusMaxSize.of(input))));
	}
}
