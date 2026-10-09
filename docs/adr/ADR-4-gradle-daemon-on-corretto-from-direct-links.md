# ADR-4: The Gradle daemon runs on Amazon Corretto, downloaded from direct CDN links

- **Status:** Accepted
- **Date:** 2026-10-08

## Context

Gradle can pin the JVM that its daemon runs on (`gradle/gradle-daemon-jvm.properties`). Gradle can
also download that JVM when the machine does not have it. By default, the download links come from
the foojay Disco API. They come through the `org.gradle.toolchains.foojay-resolver-convention`
settings plugin and the `updateDaemonJvm` task. That puts a third-party service between the build
and the JDK vendor. Also, "any vendor" makes the daemon JVM differ from machine to machine.

## Decision

- The daemon runs on **Amazon Corretto 25**: `toolchainVersion=25`, `toolchainVendor=AMAZON`.
- The download links are **direct URLs to the Corretto CDN, with a pinned version**
  (`https://corretto.aws/downloads/resources/<version>/amazon-corretto-<version>-<os>-<arch>…`).
  There is one link for each platform that Amazon builds for: Linux x64/aarch64, macOS
  x64/aarch64, Windows x64.
- **No foojay.** We removed the foojay resolver plugin from `settings.gradle.kts`. We maintain
  `gradle-daemon-jvm.properties` by hand. Do not run `updateDaemonJvm`, because it would rewrite
  the file with foojay links.
- To upgrade Corretto, replace the version in every URL. Take the version from the redirect of
  `https://corretto.aws/downloads/latest/amazon-corretto-25-x64-linux-jdk.tar.gz`.

## Consequences

- Every machine builds with the same JDK vendor and version. Each machine gets the JDK directly
  from Amazon.
- A machine without Corretto 25 downloads it into `~/.gradle/jdks` on the first build. A locally
  installed Corretto 21 or OpenJDK 25 does not satisfy the vendor criterion.
- There are no links for Windows on ARM or FreeBSD, because Corretto has no builds for them.
  Developers on these platforms must install Corretto 25 themselves.
- Pinned versions do not get JDK patch releases automatically. A version bump is a deliberate
  change.
- Gradle can no longer auto-provision Java toolchains for compilation (`jvmToolchain(…)`), because
  there is no resolver. The build does not use these toolchains today. The build compiles to JVM 11
  bytecode through `jvmTarget`, on the JDK of the daemon.
