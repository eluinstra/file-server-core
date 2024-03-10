/*
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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.vavr.collection.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public class TusMaxSizeTest
{
	@ParameterizedTest
	@MethodSource
	void testValidTusMaxSize(Long input)
	{
		assertDoesNotThrow(() -> TusMaxSize.of(input));
	}

	private static Stream<Arguments> testValidTusMaxSize()
	{
		return Stream.of(arguments(1L), arguments(1000000000000000000L), arguments(9223372036854775807L));
	}

	@ParameterizedTest
	@MethodSource
	void testEmptyTusMaxSize(Long input)
	{
		assertDoesNotThrow(() -> TusMaxSize.of(input));
	}

	private static Stream<Arguments> testEmptyTusMaxSize()
	{
		return Stream.of(arguments((Long)null), arguments(0L));
	}

	@ParameterizedTest
	@MethodSource
	void testInvalidTusMaxSize(Long input)
	{
		assertThrows(IllegalArgumentException.class, () -> TusMaxSize.of(input));
	}

	private static Stream<Arguments> testInvalidTusMaxSize()
	{
		return Stream.of(arguments(-1L), arguments(-1000000000000000000L), arguments(-9223372036854775808L));
	}
}
