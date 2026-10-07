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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.samples.petclinic.IntegrationTest;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;

/**
 * Base class for browser tests: starts the application on a random port and gives each
 * test a fresh {@link Page}.
 * <p>
 * For every failed test, a screenshot and the Playwright trace are attached to the Allure
 * report, and the trace is also saved to {@code target/playwright-traces}.
 */
@Tag("e2e")
@IntegrationTest
abstract class BrowserTestBase {

	private static final Path TRACES_DIR = Paths.get("target", "playwright-traces");

	private static Playwright playwright;

	private static Browser browser;

	@LocalServerPort
	private int port;

	private BrowserContext context;

	protected Page page;

	@RegisterExtension
	final AfterTestExecutionCallback attachToReportOnFailure = extensionContext -> {
		if (extensionContext.getExecutionException().isEmpty()) {
			context.tracing().stop();
			return;
		}
		Allure.attachment("Screenshot", "image/png", new ByteArrayInputStream(page.screenshot()),
				AttachmentOptions.withFileExtension("png"));
		Path trace = TRACES_DIR.resolve(extensionContext.getRequiredTestClass().getSimpleName() + "."
				+ extensionContext.getRequiredTestMethod().getName() + ".zip");
		context.tracing().stop(new Tracing.StopOptions().setPath(trace));
		try (InputStream content = Files.newInputStream(trace)) {
			Allure.attachment("Playwright trace", "application/zip", content,
					AttachmentOptions.withFileExtension("zip"));
		}
	};

	@BeforeAll
	static void launchBrowser() {
		playwright = Playwright.create();
		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
			.setHeadless(Boolean.parseBoolean(System.getProperty("playwright.headless", "true")));
		browser = switch (System.getProperty("playwright.browser", "chromium")) {
			case "firefox" -> playwright.firefox().launch(options);
			case "webkit" -> playwright.webkit().launch(options);
			default -> playwright.chromium().launch(options);
		};
	}

	@AfterAll
	static void closeBrowser() {
		playwright.close();
	}

	@BeforeEach
	void createContext() {
		// Fixed locale so assertions on UI texts do not depend on the machine's language
		context = browser
			.newContext(new Browser.NewContextOptions().setBaseURL("http://localhost:" + port).setLocale("en-US"));
		context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
		page = context.newPage();
	}

	@AfterEach
	void closeContext() {
		context.close();
	}

}
