package dev.luin.file.server.core;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.vavr.control.Option;

public interface ValueObjectOptional<T> extends ValueObject<Option<T>>
{
	default T getOrNull()
	{
		return getValue().getOrNull();
	}

	default T getOrElse(T other)
	{
		return getValue().getOrElse(other);
	}

	default <U> Option<U> map(Function<? super T, ? extends U> mapper)
	{
		return getValue().map(mapper);
	}

	default <U> Option<T> filter(Predicate<? super T> predicate)
	{
		return getValue().filter(predicate);
	}

	default void forEach(Consumer<? super T> action)
	{
		getValue().forEach(action);
	}
}
