package dev.luin.file.server.core.server.download;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum DownloadMethod
{
	FILE_INFO("HEAD"), DOWNLOAD_FILE("GET");
	
	String httpMethod;
	
	public static Option<DownloadMethod> of(String httpMethod)
	{
		return List.of(DownloadMethod.values())
				.filter(m -> m.httpMethod.equals(httpMethod))
				.headOption();
	}
}
