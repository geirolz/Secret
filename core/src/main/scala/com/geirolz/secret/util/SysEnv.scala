package com.geirolz.secret.util

import cats.syntax.all.*
import cats.{Applicative, MonadThrow}

trait SysEnv[F[_]]:
  def getEnv(key: String): F[Option[String]]

object SysEnv:
  inline def apply[F[_]](using ev: SysEnv[F]): SysEnv[F] = ev

  def fromMap[F[_]: Applicative](values: Map[String, String]): SysEnv[F] =
    (key: String) => values.get(key).pure[F]

  given [F[_]: MonadThrow]: SysEnv[F] with
    def getEnv(key: String): F[Option[String]] =
      MonadThrow[F].catchNonFatal(Option(System.getenv(key)))
