package dev.luin.file.server.core.server.upload;

import io.vavr.collection.List;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum UploadMethod
{
	TUS_OPTIONS("OPTIONS"), FILE_INFO("HEAD"), CREATE_FILE("POST"), UPLOAD_FILE("PATCH"), DELETE_FILE("DELETE");
	
	String httpMethod;
	
	public static Option<UploadMethod> of(String httpMethod)
	{
		return List.of(UploadMethod.values())
				.filter(m -> m.httpMethod.equals(httpMethod))
				.headOption();
	}
}
