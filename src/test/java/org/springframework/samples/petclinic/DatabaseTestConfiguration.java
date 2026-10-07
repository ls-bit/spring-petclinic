/*
 * Copyright 2012-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Starts PostgreSQL or MySQL in Docker when the {@code postgres} or {@code mysql} profile
 * is active; without either profile the application uses H2. As Spring beans, the
 * containers live as long as the application context, so all test classes sharing that
 * context share one container.
 */
@TestConfiguration(proxyBeanMethods = false)
public class DatabaseTestConfiguration {

	// Same images as docker-compose.yml

	@Bean
	@ServiceConnection
	@Profile("postgres")
	PostgreSQLContainer postgres() {
		return new PostgreSQLContainer(DockerImageName.parse("postgres:18.4"));
	}

	@Bean
	@ServiceConnection
	@Profile("mysql")
	MySQLContainer mysql() {
		return new MySQLContainer(DockerImageName.parse("mysql:9.7"));
	}

}
