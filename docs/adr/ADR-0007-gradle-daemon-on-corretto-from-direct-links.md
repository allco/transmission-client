# ADR-0007: The Gradle daemon runs on Amazon Corretto, downloaded from direct CDN links

- **Status:** Accepted
- **Date:** 2026-10-08

## Context

Gradle can pin the JVM its daemon runs on (`gradle/gradle-daemon-jvm.properties`), and download
that JVM when the machine doesn't have it. By default the download links come from the foojay
Disco API, through the `org.gradle.toolchains.foojay-resolver-convention` settings plugin and the
`updateDaemonJvm` task. That adds a third-party service between the build and the JDK vendor, and
"any vendor" makes the daemon JVM differ from machine to machine.

## Decision

- The daemon runs on **Amazon Corretto 25**: `toolchainVersion=25`, `toolchainVendor=AMAZON`.
- The download links are **direct, version-pinned Corretto CDN URLs**
  (`https://corretto.aws/downloads/resources/<version>/amazon-corretto-<version>-<os>-<arch>…`),
  one per platform Amazon builds for: Linux x64/aarch64, macOS x64/aarch64, Windows x64.
- **No foojay.** The foojay resolver plugin is removed from `settings.gradle.kts`, and
  `gradle-daemon-jvm.properties` is maintained by hand. Don't run `updateDaemonJvm`: it would
  rewrite the file with foojay links.
- Upgrading Corretto means replacing the version in every URL. Take the version from the redirect
  of `https://corretto.aws/downloads/latest/amazon-corretto-25-x64-linux-jdk.tar.gz`.

## Consequences

- Every machine builds with the same JDK vendor and version, fetched straight from Amazon.
- A machine without Corretto 25 downloads it on the first build into `~/.gradle/jdks`. The Corretto
  21 or OpenJDK 25 installed locally don't satisfy the vendor criterion.
- There are no links for Windows on ARM or FreeBSD, because Corretto has no builds for them.
  Developers there must install Corretto 25 themselves.
- Pinned versions don't pick up JDK patch releases on their own. Bumping them is a deliberate
  change.
- Java toolchains for compilation (`jvmToolchain(…)`) can no longer be auto-provisioned, since
  there's no resolver. The build doesn't use them today. Compilation targets JVM 11 bytecode
  through `jvmTarget` on the daemon's JDK.
