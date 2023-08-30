![Actions Badge](https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter/actions/workflows/git-hub-action-ci.yml/badge.svg)
![Actions Badge](https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter/actions/workflows/git-hub-action-release-ci-cd.yml/badge.svg) 

# Dynamic JWKS Spring Boot Starter

This project extends the functionality of
the [Spring Authorization Server Project](https://github.com/spring-projects/spring-authorization-server).  
The goal of the project is to add automatic certificate rotation without losing the ability to horizontally scale the
authorization server.

The project is provided in the form of three modules:

- dynamic-jwks
- dynamic-vault-jwks-spring-boot
- dynamic-vault-jwks-spring-boot-starter

Dependencies check analyze:
```shell
gradlew clean dependencyCheckAnalyze
```

## Quick Start

In order to use this starter it is necessary to add it to the project dependencies.

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

Maven repository group page [https://mvnrepository.com/artifact/io.github.world-wide-development](https://mvnrepository.com/artifact/io.github.world-wide-development)

## Building From Source

```shell
gradlew clean build
```

If you're on Windows, that may be why the `gradlew` file did not get added as executable in your repository. On Windows,
you can run:

```shell
git update-index --chmod=+x gradlew
```
