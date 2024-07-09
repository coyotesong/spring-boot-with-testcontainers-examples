# Database testing with Hamcrest

This is future work.

I've had great success with custom Hamcrest matchers for database testing
and plan to add them to this repo.

The gist with the test container versions

- have constructors that accept a testcontainer instance
- have independent implementations of a few simple JDBC functions

The Hamcrest matchers are

- tableExists(name)
- tableMatches(name, predicate)
- recordExists(name, query)
- uniqueRecordExists(name, query)
- recordMatches(name, query, predicate)
- uniqueRecordMatches(name, query, predicate)

The predicates are required, esp. with record matching, since we
often only care about a handful of fields and/or may wish to use
soft matching.

This selective filtering is critical when there are auto-updated
audit fields like timestamps. (The matcher can be smart enough to
ignore `serial` fields.)
