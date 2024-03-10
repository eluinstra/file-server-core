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
package dev.luin.file.server.core.server.download.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

import dev.luin.file.server.core.server.download.DownloadException;
import io.vavr.control.Either;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@FieldDefaults(level = AccessLevel.PRIVATE)
@TestInstance(value = Lifecycle.PER_CLASS)
public class IfRangeTest
{
	Either<DownloadException, Date> expectedTime = Either.right(Date.from(LocalDateTime.of(1994, Month.NOVEMBER, 6, 8, 49, 37).toInstant(ZoneOffset.UTC)));

	@Test
	public void testValidHTTPDate_IMF_FIXDATE() throws ParseException
	{
		val actualTime = IfRange.getDate("Sun, 06 Nov 1994 08:49:37 GMT");
		assertThat(actualTime).isEqualTo(expectedTime);
	}

	@Test
	public void testValidHTTPDate_RFC_850() throws ParseException
	{
		val actualTime = IfRange.getDate("Sunday, 06-Nov-94 08:49:37 GMT");
		assertThat(actualTime).isEqualTo(expectedTime);
	}

	@Test
	public void testValidHTTPDate_ANSI_C() throws ParseException
	{
		val actualTime = IfRange.getDate("Sun Nov  6 08:49:37 1994");
		assertThat(actualTime).isEqualTo(expectedTime);
	}

	@Test
	public void testInvalidHTTPDate() throws ParseException
	{
		val actualTime = IfRange.getDate("Invalid date");
		assertThat(actualTime).hasLeftValueSatisfying(t -> assertThat(t.toHttpException().getStatusCode()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST));
	}

}
