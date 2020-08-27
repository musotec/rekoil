package tech.muso.rekoil

class RekoilDependencyException(string: String?) : RuntimeException(string)

class RekoilLazyNodeRegistrationException : RuntimeException()