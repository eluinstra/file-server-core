package dev.luin.file.server.core.file.encryption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.Value;

@Value
public class EncryptionProperties implements ValueObject<String>
{
  @NonNull
  byte[] certificate;
  @NonNull
  byte[] encryptionSecret;

	public static EncryptionProperties create(String value)
	{
		return Try.of(() -> new ObjectMapper().readValue(value, EncryptionProperties.class)).get();
	}

	@Override
	@JsonIgnore
	public String getValue()
	{
		return Try.of(() -> new ObjectMapper().writeValueAsString(this)).get();
	}
}
