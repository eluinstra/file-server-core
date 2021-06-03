package dev.luin.file.server.core;

import java.util.function.Function;

import io.vavr.control.Option;

public interface ValueObjectOptional<T> extends ValueObject<Option<T>>
{
	default T getOrNull()
	{
		return getValue().getOrNull();
	}

	default <U> Option<U> map(Function<? super T, ? extends U> mapper)
	{
		return getValue().map(mapper);
	}
}
