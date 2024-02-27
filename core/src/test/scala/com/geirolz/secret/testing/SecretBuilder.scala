package com.geirolz.secret.testing

import com.geirolz.secret.{OneShotSecret, Secret}
import com.geirolz.secret.internal.SecretApi
import com.geirolz.secret.strategy.SecretStrategy
import com.geirolz.secret.util.Hasher

trait SecretBuilder[S[X] <: SecretApi[X]]:
  val name: String
  def apply[T](value: => T, collectDestructionLocation: Boolean = true)(using
    strategy: SecretStrategy[T],
    hasher: Hasher
  ): S[T]

object SecretBuilder:
  val secret: SecretBuilder[Secret] =
    new SecretBuilder[Secret]:
      override val name: String = "Secret"
      override def apply[T](value: => T, collectDestructionLocation: Boolean = true)(using
        strategy: SecretStrategy[T],
        hasher: Hasher
      ): Secret[T] = Secret(value, collectDestructionLocation)

  val oneShotSecret: SecretBuilder[OneShotSecret] =
    new SecretBuilder[OneShotSecret]:
      override val name: String = "OneShotSecret"
      override def apply[T](value: => T, collectDestructionLocation: Boolean = true)(using
        strategy: SecretStrategy[T],
        hasher: Hasher
      ): OneShotSecret[T] = OneShotSecret(value, collectDestructionLocation)
