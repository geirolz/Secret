import sbt.*
import scala.language.postfixOps

object ProjectDependencies {

  // base
  private val catsVersion   = "2.12.0"
  private val bcryptVersion = "0.10.2"
  // test
  private val munitVersion       = "1.0.0"
  private val munitEffectVersion = "2.0.0"
  private val scalacheck         = "1.18.0"
  // integrations
  private val catsEffectVersion     = "3.5.4"
  private val pureConfigVersion     = "0.17.7"
  private val typesafeConfigVersion = "1.4.3"
  private val cirisVersion          = "3.6.0"

  lazy val common: Seq[ModuleID] = Seq(
    // base
    "org.typelevel" %% "cats-core" % catsVersion,
    "at.favre.lib"   % "bcrypt"    % bcryptVersion,

    // test
    "org.typelevel"  %% "cats-effect"       % catsEffectVersion  % Test,
    "org.typelevel"  %% "munit-cats-effect" % munitEffectVersion % Test,
    "org.scalameta"  %% "munit"             % munitVersion       % Test,
    "org.scalameta"  %% "munit-scalacheck"  % munitVersion       % Test,
    "org.scalacheck" %% "scalacheck"        % scalacheck         % Test
  )

  object Core {
    lazy val dedicated: Seq[ModuleID] = Nil
  }

  object Integrations {

    object CatsEffect {
      lazy val dedicated: Seq[ModuleID] = List(
        "org.typelevel" %% "cats-effect"       % catsEffectVersion,
        "org.typelevel" %% "munit-cats-effect" % munitEffectVersion % Test
      )
    }

    object Pureconfig {
      lazy val dedicated: Seq[ModuleID] = List(
        "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion
      )
    }

    object TypesafeConfig {
      lazy val dedicated: Seq[ModuleID] = List(
        "com.typesafe" % "config" % typesafeConfigVersion
      )
    }

    object Ciris {
      lazy val dedicated: Seq[ModuleID] = List(
        "is.cir"        %% "ciris"             % cirisVersion,
        "org.typelevel" %% "cats-effect"       % catsEffectVersion  % Test,
        "org.typelevel" %% "munit-cats-effect" % munitEffectVersion % Test
      )
    }

    object Circe {
      lazy val dedicated: Seq[ModuleID] = List(
        "io.circe" %% "circe-core" % "0.14.9"
      )
    }

    object CatsXml {
      lazy val dedicated: Seq[ModuleID] = List(
        "com.github.geirolz" %% "cats-xml" % "0.0.15"
      )
    }
  }

  object Plugins {
    val compilerPlugins: Seq[ModuleID] = Nil
  }

  object Docs {
    lazy val dedicated: Seq[ModuleID] = Nil
  }
}
