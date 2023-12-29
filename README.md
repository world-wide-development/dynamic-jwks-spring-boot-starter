[![License Badge](https://img.shields.io/github/license/world-wide-development/dynamic-jwks-spring-boot-starter)](https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter/blob/release/0.0.x/LICENSE)
![Actions Badge](https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter/actions/workflows/git-hub-action-ci.yml/badge.svg)
![Actions Badge](https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter/actions/workflows/git-hub-action-release-ci-cd.yml/badge.svg)
[![Maven Central Badge](https://img.shields.io/maven-central/v/io.github.world-wide-development/dynamic-jwks)](https://mvnrepository.com/artifact/io.github.world-wide-development)

# Dynamic JWKS Spring Boot Starter

This project extends the functionality of
the [Spring Authorization Server Project](https://github.com/spring-projects/spring-authorization-server).  
The goal of the project is to add automatic certificate rotation without losing the ability to horizontally scale the
authorization server.

## Quick Start for Dynamic Redis JWKS

To use this starter, it is necessary to add it to the project dependencies.

Gradle:

```groovy
implementation "io.github.world-wide-development:dynamic-redis-jwks-spring-boot-starter:${latestVersion}"
```

Maven:

```xml

<dependency>
    <groupId>io.github.world-wide-development</groupId>
    <artifactId>dynamic-redis-jwks-spring-boot-starter</artifactId>
    <version>${latest-version}</version>
</dependency>
```

Maven repository group
page [https://mvnrepository.com/artifact/io.github.world-wide-development](https://mvnrepository.com/artifact/io.github.world-wide-development)

## Quick Start for Dynamic Vault JWKS

To use this starter, it is necessary to add it to the project dependencies.

Gradle:

```groovy
implementation "io.github.world-wide-development:dynamic-vault-jwks-spring-boot-starter:${latestVersion}"
```

Maven:

```xml

<dependency>
    <groupId>io.github.world-wide-development</groupId>
    <artifactId>dynamic-vault-jwks-spring-boot-starter</artifactId>
    <version>${latest-version}</version>
</dependency>
```

Maven repository group
page [https://mvnrepository.com/artifact/io.github.world-wide-development](https://mvnrepository.com/artifact/io.github.world-wide-development)

## Useful scripts

Building From Source

```shell
gradlew clean build
```

Dependencies check analyze:

```shell
gradlew clean dependencyCheckAnalyze
```

Update `gradlew` file owner:

```shell
git update-index --chmod=+x gradlew
```
