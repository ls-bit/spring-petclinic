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

package org.springframework.samples.petclinic.e2e;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.samples.petclinic.IntegrationTest;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

/**
 * Base class for API tests: starts the application on a random port and gives each test a
 * fresh {@link APIRequestContext}, which sends HTTP requests without a browser.
 */
@Tag("e2e")
@IntegrationTest
abstract class ApiTestBase {

	private static Playwright playwright;

	@LocalServerPort
	private int port;

	protected APIRequestContext request;

	@BeforeAll
	static void createPlaywright() {
		playwright = Playwright.create();
	}

	@AfterAll
	static void closePlaywright() {
		playwright.close();
	}

	@BeforeEach
	void createRequestContext() {
		request = playwright.request()
			.newContext(new APIRequest.NewContextOptions().setBaseURL("http://localhost:" + port));
	}

	@AfterEach
	void disposeRequestContext() {
		request.dispose();
	}

}
