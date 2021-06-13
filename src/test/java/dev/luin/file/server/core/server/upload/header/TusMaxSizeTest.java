/**
 * Copyright 2020 E.Luinstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
