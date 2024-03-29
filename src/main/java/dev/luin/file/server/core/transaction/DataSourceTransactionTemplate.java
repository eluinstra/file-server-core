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
package dev.luin.file.server.core.transaction;

import java.util.function.Supplier;

import org.springframework.transaction.annotation.Transactional;

public class DataSourceTransactionTemplate implements TransactionTemplate
{
	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public void executeTransaction(Runnable runnable)
	{
		runnable.run();
	}

	@Override
	@Transactional(transactionManager = "dataSourceTransactionManager")
	public <T> T executeTransactionWithResult(Supplier<T> function)
	{
		return function.get();
	}
}
