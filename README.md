![Actions Badge](https://github.com/world-wide-development/dynamic-jwks-spring-boot-starter/actions/workflows/maven-release-ci-cd.yml/badge.svg)

## Dynamic JWKS Spring Boot Starter

This project extends the functionality of
the [Spring Authorization Server Project](https://github.com/spring-projects/spring-authorization-server).  
The goal of the project is to add automatic certificate rotation without losing the ability to horizontally scale the
authorization server.

The project is provided in the form of three modules:

- dynamic-jwks
- dynamic-vault-jwks-spring-boot
- dynamic-vault-jwks-spring-boot-starter

## Building From Source

```shell
gradlew clean build
```

If you're on Windows, that may be why the `gradlew` file did not get added as executable in your repository. On Windows,
you can run:

```shell
git update-index --chmod=+x gradlew
```
