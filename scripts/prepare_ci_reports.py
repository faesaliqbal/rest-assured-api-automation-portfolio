"""Export only result fields safe for CI artifacts; keep raw diagnostics in job logs."""

from pathlib import Path
import json
import xml.etree.ElementTree as ET


def prepare_reports(source=Path("target/surefire-reports"),
                    destination=Path("target/ci-reports")):
    reports = sorted(source.glob("TEST-*.xml"))
    if not reports:
        print("No Surefire XML reports found; no test-result artifact generated.")
        return

    result = ET.Element("testsuites")
    totals = dict(tests=0, failures=0, errors=0, skipped=0)
    for report in reports:
        raw = ET.parse(report).getroot()
        suite = ET.SubElement(result, "testsuite", {
            key: raw.attrib[key]
            for key in ("name", "tests", "failures", "errors", "skipped", "time")
            if key in raw.attrib
        })
        for key in totals:
            totals[key] += int(raw.get(key, "0"))
        for case in raw.findall("testcase"):
            clean = ET.SubElement(suite, "testcase", {
                key: case.attrib[key]
                for key in ("name", "classname", "time")
                if key in case.attrib
            })
            for outcome in ("failure", "error", "skipped"):
                if case.find(outcome) is not None:
                    ET.SubElement(clean, outcome, {
                        "message": "See the CI job log for diagnostic details."
                    })
        # Omit properties, hostnames, paths, raw bodies, messages, and stack traces.

    destination.mkdir(parents=True, exist_ok=True)
    ET.indent(result, space="  ")
    ET.ElementTree(result).write(destination / "junit-results.xml",
                                 encoding="utf-8", xml_declaration=True)
    (destination / "summary.json").write_text(
        json.dumps(totals, indent=2) + "\n", encoding="utf-8")
    print(f"Prepared CI results: {totals}")


if __name__ == "__main__":
    prepare_reports()
