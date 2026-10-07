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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

class ApiE2ETests extends ApiTestBase {

	@Test
	void listsVetsAsJson() {
		APIResponse response = request.get("/vets");

		assertThat(response).isOK();
		assertThat(response.headers().get("content-type")).startsWith("application/json");
		String body = response.text();
		List<String> lastNames = JsonPath.read(body, "$.vetList[*].lastName");
		assertThat(lastNames).containsExactlyInAnyOrder("Carter", "Leary", "Douglas", "Ortega", "Stevens", "Jenkins");
		List<String> douglasSpecialties = JsonPath.read(body,
				"$.vetList[?(@.lastName == 'Douglas')].specialties[*].name");
		assertThat(douglasSpecialties).containsExactlyInAnyOrder("surgery", "dentistry");
	}

	@Test
	void reportsHealthUp() {
		APIResponse response = request.get("/actuator/health");

		assertThat(response).isOK();
		assertThat((String) JsonPath.read(response.text(), "$.status")).isEqualTo("UP");
	}

	@Test
	void createsOwnerAndRedirectsToDetails() {
		APIResponse response = request.post("/owners/new",
				RequestOptions.create()
					.setForm(FormData.create()
						.set("firstName", "Ada")
						.set("lastName", "Lovelace")
						.set("address", "12 St James's Square")
						.set("city", "London")
						.set("telephone", "2075550100"))
					.setMaxRedirects(0));

		assertThat(response.status()).isEqualTo(302);
		String ownerUrl = response.headers().get("location");
		assertThat(ownerUrl).containsPattern("/owners/\\d+");
		assertThat(request.get(ownerUrl).text()).contains("Ada Lovelace", "London");
	}

	@Test
	void rejectsOwnerWithInvalidTelephone() {
		APIResponse response = request.post("/owners/new",
				RequestOptions.create()
					.setForm(FormData.create()
						.set("firstName", "Ada")
						.set("lastName", "Lovelace")
						.set("address", "12 St James's Square")
						.set("city", "London")
						.set("telephone", "not a number"))
					.setMaxRedirects(0));

		// The form is shown again with the error instead of redirecting
		assertThat(response.status()).isEqualTo(200);
		assertThat(response.text()).contains("Telephone must be a 10-digit number");
	}

}
