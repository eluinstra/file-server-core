/**
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
package dev.luin.file.server.core.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.sql.SQLQueryFactory;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceConfig
{
	@Autowired
	SQLQueryFactory queryFactory;

	@Bean
	public AuthenticationManager authenticationManager()
	{
		return new AuthenticationManager(userManager());
	}

	@Bean
	public UserManager userManager()
	{
		return new UserManager(userDAO());
	}

	@Bean
	public UserService userService()
	{
		return new UserServiceImpl(userDAO());
	}

	@Bean
	public UserDAO userDAO()
	{
		return new UserDAOImpl(queryFactory);
	}
}
