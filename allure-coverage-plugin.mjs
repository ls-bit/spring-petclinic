import { existsSync, readFileSync } from "node:fs";
import { readdir, readFile } from "node:fs/promises";
import { join, relative } from "node:path";

function percent(covered, missed) {
  const total = covered + missed;
  // Rounded down, as in the JaCoCo report
  return total === 0 ? "n/a" : `${Math.floor((100 * covered) / total)}% (${covered} of ${total})`;
}

/** Reads the totals of a JaCoCo HTML report directory, or returns undefined if there is none. */
export function readCoverage(directory) {
  const csv = join(directory, "jacoco.csv");
  if (!existsSync(csv)) {
    return undefined;
  }
  const [header, ...rows] = readFileSync(csv, "utf8").trim().split("\n").map((line) => line.split(","));
  const sum = (column) => rows.reduce((total, row) => total + Number(row[header.indexOf(column)]), 0);
  return {
    instructions: { covered: sum("INSTRUCTION_COVERED"), missed: sum("INSTRUCTION_MISSED") },
    branches: { covered: sum("BRANCH_COVERED"), missed: sum("BRANCH_MISSED") },
  };
}

export function coverageSummary(coverage) {
  return {
    instructions: percent(coverage.instructions.covered, coverage.instructions.missed),
    branches: percent(coverage.branches.covered, coverage.branches.missed),
  };
}

/**
 * Copies a JaCoCo HTML report into the Allure report and adds it as a card on the report's start page.
 * Options: `directory` of the JaCoCo report and `name` of the card.
 */
export default class CoveragePlugin {
  constructor(options) {
    this.options = options;
  }

  async done(context) {
    const { directory } = this.options;
    for (const entry of await readdir(directory, { recursive: true, withFileTypes: true })) {
      if (entry.isFile()) {
        const file = join(entry.parentPath, entry.name);
        await context.reportFiles.addFile(relative(directory, file), await readFile(file));
      }
    }
  }

  async info() {
    const { instructions } = readCoverage(this.options.directory);
    // The start page shows stats as a ring of passed/total, so instructions are passed in as "tests"
    return {
      name: this.options.name,
      plugin: "JaCoCo",
      stats: { total: instructions.covered + instructions.missed, passed: instructions.covered },
      status: "unknown",
      duration: 0,
    };
  }
}
