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

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

class OwnerE2ETests extends BrowserTestBase {

	@Test
	void findsSingleOwnerByLastName() {
		page.navigate("/owners/find");

		page.locator("#lastName").fill("Franklin");
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner")).click();

		assertThat(page).hasURL(Pattern.compile("/owners/\\d+$"));
		assertThat(page.locator("table").first()).containsText("George Franklin");
	}

	@Test
	void listsOwnersWithSameLastName() {
		page.navigate("/owners/find");

		page.locator("#lastName").fill("Davis");
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner")).click();

		assertThat(page.locator("#owners tbody tr")).hasCount(2);
	}

	@Test
	void reportsUnknownLastName() {
		page.navigate("/owners/find");

		page.locator("#lastName").fill("Unknown");
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Find Owner")).click();

		assertThat(page.locator("#search-owner-form")).containsText("has not been found");
	}

	@Test
	void addsNewOwner() {
		page.navigate("/owners/new");

		page.getByLabel("First Name").fill("Grace");
		page.getByLabel("Last Name").fill("Hopper");
		page.getByLabel("Address").fill("1 Navy Yard");
		page.getByLabel("City").fill("Arlington");
		page.getByLabel("Telephone").fill("7035550100");
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner")).click();

		assertThat(page.locator("#success-message")).hasText("New Owner Created");
		assertThat(page.locator("table").first()).containsText("Grace Hopper");
	}

	@Test
	void rejectsOwnerWithoutRequiredFields() {
		page.navigate("/owners/new");

		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Owner")).click();

		assertThat(page).hasURL(Pattern.compile("/owners/new$"));
		assertThat(page.locator(".has-error")).hasCount(5);
	}

}
