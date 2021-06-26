package dev.luin.file.server.core;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;

import io.vavr.Function1;
import io.vavr.Function2;

public class Common
{
	public static final Function1<Object,Void> toNull = o -> null;

	public static final Function2<Logger,String,Consumer<Object>> logObject = (log,message) -> o -> log.info(message,o);

	public static final Function1<Throwable,IOException> toIOException = t -> t instanceof IOException ? (IOException)t : new IOException(t);
}
