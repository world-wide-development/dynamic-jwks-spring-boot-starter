![Actions Badge](https://github.com/world-wide-development/vault-dynamic-jwks-spring-boot-starter-project/actions/workflows/github-ci.yml/badge.svg)
## Vault Dynamic JWKS Spring Boot Starter Project
This project extends the functionality of the [Spring Authorization Server Project](https://github.com/spring-projects/spring-authorization-server).  
The goal of the project is to add automatic certificate rotation without losing the ability to horizontally scale the authorization server.  
Hashi Corp Vault is responsible for issuing and storing the certificates.   
The project is provided in the form of three modules:
- vault-dynamic-jwks
- vault-dynamic-jwks-spring-boot
- vault-dynamic-jwks-spring-boot-starter

## Building From Source
```shell
gradlew clean build
```
If you're on Windows, that may be why the `gradlew` file did not get added as executable in your repository. On Windows, you can run:

```shell
git update-index --chmod=+x gradlew
```
