# spring-boot-with-testcontainers-examples

Examples of using Spring Boot with TestContainers

Test-Driven Development (TDD) is an incredibly powerful approach to software
development but it requires working servers. (See note below regarding SAAS
services.) TestContainers - transient docker and cloud-based instances of
the actual servers - make this a relatively painfree process. (See note below.)

That said I've encountered a pretty steep hurdle in practice - the Spring Boot
integration with TestContainers has changed rapidly and search results are
often only valid for a different version of Spring Boot or other dependencies.
It take easly take several hours of trial-and-error to find a working combination
for your environment.

This repo is intended to fix that. It will have working maven projects that
work for the specified version of Bpring Boot.

## Testing Unimplemented or Unavailable Services

It's common for the client and server to be developed at the same time. In these
cases we can use [Mockserver](https://testcontainers.com/modules/mockserve/)
or [WireMock](https://testcontainers.com/modules/wiremock/) for our TDD tests.

If this isn't enough we can create a minimal Spring Boot (etc) implementation
that implements the API on top of a heavily mocked business layer.

## Testing SAAS Services

In many cases we can mock SAAS services by using a capture-and-replay proxy
server. This could be an actual proxy (e.g., a squid server) or the mocked
servers mentioned above.

This approach may require additional work if the messages include timestamps.
In some cases the replayed messages can be updated on the fly, but in other
cases this will be impossible due to the use of digital signatures on the
messages.

## Docker on Apple Silicon

This may no longer be a problem, esp. now that a cloud-based solution is
available, but I had problems using TestContainers on Apple Silicon in the
2022-23 period due to changes in docker's licensing terms. I'll update this
section as I learn more.

