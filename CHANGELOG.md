Change Log
==========

Version 0.0.1 *(2020-06-16)*
----------------------------

Initial commit for pre-release version.

Version 0.0.2 *(2020-08-14)*
----------------------------
- Fix bottleneck in emission of `RekoilValues`
    - Most recent valid value will be emitted, but updates in quick succession may not be received by subscriptions.
    - Values are only emitted if the value has changed since last received, even if it appears to be the same.
        - e.g. Set order of `true -> false -> true` may be received as `true -> true`.
- Fix inheritance of `RekoilScopes` within `Selectors`
    - Selectors can now properly call `get(rekoilValue)` to get values from a parent when nest depth > 1
- Add logic to return to suspended Selector evaluation coroutines for nodes that have not yet been instantiated
    - Allows for selectors in a class to call `get(atom)` on an atom defined lower in the file
- Added kotlin extension functions for the creation of delegate properties
    - Allows syntactic sugar to simplify `atom.value = "new value"` to `atomDelegate = "new value"`
    - Allows for simple reading of variable directly without dependency registration
- Improve logging, which can be enabled from DbgHelpers.kt by setting debug to true
- Clean up structure of Selector/Rekoil scope implementation, but there is still much clean up to be done.
