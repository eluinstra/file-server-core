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
package dev.luin.file.server.core.service.user;

import com.querydsl.sql.SQLQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceConfig
{
	@Bean
	public AuthenticationManager authenticationManager(@Autowired UserManager userManager)
	{
		return new AuthenticationManager(userManager);
	}

	@Bean
	public UserManager userManager(@Autowired UserDAO userDAO)
	{
		return new UserManager(userDAO);
	}

	@Bean
	public UserService userService(@Autowired UserDAO userDAO)
	{
		return new UserServiceImpl(userDAO);
	}

	@Bean
	public UserDAO userDAO(@Autowired SQLQueryFactory queryFactory)
	{
		return new UserDAOImpl(queryFactory);
	}
}
