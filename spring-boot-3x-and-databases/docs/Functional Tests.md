# Functional Tests

We have traditionally made a distinction between unit tests (entirely self-contained)
and integration tests (relying on external resources). Tests using embedded servers and
test containers fall into a middle ground since they can be run in isolation, like unit
tests, but can take a long time to run, like integration tests.

For maximum flexibility all of the test-container based tests have been put in a new
test source folder: `functional-test`. With small changes to the parent pom.xml file
these tests can be run as either unit or integration tests, depending on the best fit
for local needs.

Since I'm practicing TDD - which places a high value on quick tests - the tests are
currently set up to run as integration tests.
