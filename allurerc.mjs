import { fileURLToPath } from "node:url";
import { coverageSummary, readCoverage } from "./allure-coverage-plugin.mjs";

const pluginPath = fileURLToPath(new URL("./allure-coverage-plugin.mjs", import.meta.url));

// JaCoCo reports written by the build, see the Test report section in README.md
const coverageReports = [
  { id: "coverage-all", directory: "target/site/jacoco-all", label: "All tests" },
  { id: "coverage-unit", directory: "target/site/jacoco", label: "Unit and integration tests" },
  { id: "coverage-e2e", directory: "target/site/jacoco-e2e", label: "E2E tests" },
].filter((report) => readCoverage(report.directory));

const variables = {};
const plugins = { awesome: { options: { reportName: "Tests" } } };
for (const { id, directory, label } of coverageReports) {
  const summary = coverageSummary(readCoverage(directory));
  variables[`${label}: instruction coverage`] = summary.instructions;
  variables[`${label}: branch coverage`] = summary.branches;
  plugins[id] = { import: pluginPath, options: { directory, name: `Code coverage: ${label.toLowerCase()}` } };
}

export default {
  name: "PetClinic tests",
  variables,
  plugins,
};
