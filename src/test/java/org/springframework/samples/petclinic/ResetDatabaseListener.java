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

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Resets the database to the sample data before each test, using the scripts of the
 * database the application runs with. The scripts are chosen at runtime because Spring
 * AOT would need {@code @Sql} script paths to be resolvable at build time.
 */
public class ResetDatabaseListener extends AbstractTestExecutionListener {

	@Override
	public void beforeTestMethod(TestContext testContext) {
		ApplicationContext context = testContext.getApplicationContext();
		String database = context.getEnvironment().getRequiredProperty("database");
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
				context.getResource("classpath:db/clear-tables-" + database + ".sql"),
				context.getResource("classpath:db/" + database + "/data.sql"));
		populator.execute(context.getBean(DataSource.class));
	}

}
