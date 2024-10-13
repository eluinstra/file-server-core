package dev.luin.file.server.core.file.encryption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.luin.file.server.core.ValueObject;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Encryption implements ValueObject<String>
{
  @NonNull
  EncryptionAlgorithm algorithm;
  EncryptionProperties properties;

  @Override
	@JsonIgnore
  public String getValue()
  {
		return Try.of(() -> new ObjectMapper().writeValueAsString(this)).get();
  }
}
