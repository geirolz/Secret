# Secret

[![Build Status](https://github.com/geirolz/secret/actions/workflows/cicd.yml/badge.svg)](https://github.com/geirolz/secret/actions)
[![codecov](https://img.shields.io/codecov/c/github/geirolz/secret)](https://codecov.io/gh/geirolz/secret)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db3274b55e0c4031803afb45f58d4413)](https://www.codacy.com/manual/david.geirola/secret?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=geirolz/secret&amp;utm_campaign=Badge_Grade)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/com.github.geirolz/secret_2.13?server=https%3A%2F%2Foss.sonatype.org)](https://mvnrepository.com/artifact/com.github.geirolz/secret)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/geirolz/secret&style=flat)](https://mergify.io)
[![GitHub license](https://img.shields.io/github/license/geirolz/secret)](https://github.com/geirolz/secret/blob/main/LICENSE)

A functional, type-safe and memory-safe class to handle secret values 

`Secret` does the best to avoid leaking information in memory and in the code BUT an attack is possible and I don't give any certainties or
guarantees about security using this class, you use it at your own risk. Code is open source, you can check the implementation and take your
decision consciously. I'll do my best to improve the security and documentation of this class.

Please, drop a ⭐️ if you are interested in this project and you want to support it.


## Obfuscation

The value is obfuscated when creating the `Secret` instance using the implicit `ObfuscationStrategy` which, by default, transform the value into a xor-ed
`ByteBuffer` witch store bytes outside the JVM using direct memory access.

The obfuscated value is de-obfuscated using the implicit `ObfuscationStrategy` instance every time the method `use` is invoked which returns the original
value converting bytes back to `T` re-apply the xor.


## API and Type safety

While obfuscating the value prevents or at least makes it harder to read the value from memory, Secret class API are designed to avoid leaking
information in other ways. Preventing developers to improperly use the secret value ( logging, etc...).

Example
```mdoc scala
  import com.github.geirolz.secret.Secret
  
  val secretString: Secret[String]  = Secret("my_password")
  val database: F[Database]         = secretString.use(password => initDb(password))
```

## Getting Started

To get started with Secret, follow these steps:

1. **Installation:** Include the library as a dependency in your Scala project. You can find the latest version and
   installation instructions in the [Secret GitHub repository](https://github.com/geirolz/secret).


Scala2.13
```sbt
libraryDependencies += "com.github.geirolz" %% "secret_3" % "@VERSION@"
```

Scala3
```sbt
libraryDependencies += "com.github.geirolz" %% "secret" % "@VERSION@"
```

### Integrations

These integrations aim to enhance the functionality and capabilities of your applications by leveraging the features and
strengths of both Secret and the respective libraries.

#### Pureconfig
```sbt
libraryDependencies += "com.github.geirolz" %% "secret-pureconfig" % "@VERSION@"
```
## Adopters

If you are using Secret in your company, please let me know and I'll add it to the list! It means a lot to me.

## Contributing

We welcome contributions from the open-source community to make Secret even better. If you have any bug reports,
feature requests, or suggestions, please submit them via GitHub issues. Pull requests are also welcome.

Before contributing, please read
our [Contribution Guidelines](https://github.com/geirolz/secret/blob/main/CONTRIBUTING.md) to understand the
development process and coding conventions.

Please remember te following:

- Run `sbt scalafmtAll` before submitting a PR.
- Run `sbt gen-doc` to update the documentation.

## License

Secret is released under the [Apache License 2.0](https://github.com/geirolz/secret/blob/main/LICENSE).
Feel free to use it in your open-source or commercial projects.

## Acknowledgements
- https://westonal.medium.com/protecting-strings-in-jvm-memory-84c365f8f01c
- VisualVM
- ChatGPT
- Personal experience in companies where I worked
