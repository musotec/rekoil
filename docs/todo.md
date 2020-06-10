# To Do (by soon)

Here's a working todo list of the features I have planned for this implementation. They may or may not be implemented up-stream.
This list is not prioritized (as of 6/12/20).

## Rekoil Components

### Scopes
* Allow for Atoms and Selectors to be shared between scopes
* Add global scope for use across files
* Implement operators (union and intersection of scopes)
* Implementation performance optimization

### Atoms
* Add ability to update Atom value asynchronously
* Add un-cached Atoms, that are always evaluated upon retrieval

### Selectors
* Add ability to update a Selector transformation function
* Add different Selector varieties and operations for convenience

### General
* Add Kotlin Contracts to library for compiler optimization
* Add Kotlin Flow accessors
* Implement (and test) Kotlin/JS and Kotlin/Native

## Documentation
* Prettify the Github README.md
* Create a website for displaying the markdown guide using [MkDocs](https://squidfunk.github.io/mkdocs-material/getting-started/#installation)
* Create [Dokka](https://github.com/Kotlin/dokka) generated documentation pages matching the [Kotlin standard library format](https://kotlin.github.io/kotlinx.coroutines/)

## Examples
* Add Android example project
* Add example migration project & guide
* Add example of interoperability with RxJava, Android LiveData, and Java Flow.