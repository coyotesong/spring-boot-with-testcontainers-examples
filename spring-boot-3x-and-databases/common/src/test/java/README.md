# Common Module - Tests

This module has few tests - they're intended to ensure that we have the proper
integration between Spring Boot and TestContainer configurations.

The real tests are preformed in the 'shared-tests' module.

## Important note

The tests often end with a series of

```
"2024-07-15 15:49:11.694 WARN  [Container.postgres$16.3:112] - [83] FATAL:  terminating connection due to unexpected postmaster exit
"2024-07-15 15:49:11.695 WARN  [Container.postgres$16.3:112] - [95] FATAL:  terminating connection due to unexpected postmaster exit
"2024-07-15 15:49:11.696 WARN  [Container.postgres$16.3:112] - [79] FATAL:  terminating connection due to unexpected postmaster exit
"2024-07-15 15:49:11.701 WARN  [Container.postgres$16.3:112] - [87] FATAL:  terminating connection due to unexpected postmaster exit
"2024-07-15 15:49:11.702 WARN  [Container.postgres$16.3:112] - [91] FATAL:  terminating connection due to unexpected postmaster exit
"2024-07-15 15:49:11.703 WARN  [Container.postgres$16.3:112] - [85] FATAL:  terminating connection due to unexpected postmaster exit
"2024-07-15 15:49:11.703 WARN  [Container.postgres$16.3:112] - [81] FATAL:  terminating connection due to unexpected postmaster exit
```

This is fine. It appears to be related to the use of Hikari for the connection pool.
This is an intentional choice - I think it may expose unexpected behavior with a bit 
of work - but it may have race conditions with the standard container shutdown.