package com.geirolz.secret

import cats.{Eq, Show}
import com.geirolz.secret.Secret.*
import com.geirolz.secret.internal.KeyValueBuffer

import scala.util.Try
import scala.util.hashing.Hashing

/** Memory-safe and type-safe secret value of type `T`.
  *
  * `Secret` does the best to avoid leaking information in memory and in the code BUT an attack is possible and I don't give any certainties or
  * guarantees about security using this class, you use it at your own risk. Code is open source, you can check the implementation and take your
  * decision consciously. I'll do my best to improve the security and documentation of this class.
  *
  * <b>Obfuscation</b>
  *
  * The value is obfuscated when creating the `Secret` instance using the given `ObfuscationStrategy` which, by default, transform the value into a
  * xor-ed `ByteBuffer` witch store bytes outside the JVM using direct memory access.
  *
  * The obfuscated value is de-obfuscated using the given `ObfuscationStrategy` instance every time the method `use` is invoked which returns the
  * original value converting bytes back to `T` re-apply the xor.
  *
  * <b>API and Type safety</b>
  *
  * While obfuscating the value prevents or at least makes it harder to read the value from memory, Secret class API are designed to avoid leaking
  * information in other ways. Preventing developers to improperly use the secret value ( logging, etc...).
  *
  * Example
  * {{{
  *   val secretString: Secret[String]  = Secret("my_password")
  *   val database: F[Database]         = secretString.use(password => initDb(password))
  * }}}
  */
trait Secret[T] extends AutoCloseable:

  import cats.syntax.all.*

  /** Apply `f` with the de-obfuscated value WITHOUT destroying it.
    *
    * If the secret is destroyed it will raise a `NoLongerValidSecret` exception.
    *
    * Once the secret is destroyed it can't be used anymore. If you try to use it using `use`, `useAndDestroy`, `evalUse`, `evalUseAndDestroy` and
    * other methods, it will raise a `NoLongerValidSecret` exception.
    */
  def evalUse[F[_]: MonadSecretError, U](f: T => F[U]): F[U]

  /** Destroy the secret value by filling the obfuscated value with '\0'.
    *
    * This method is idempotent.
    *
    * Once the secret is destroyed it can't be used anymore. If you try to use it using `use`, `useAndDestroy`, `evalUse`, `evalUseAndDestroy` and
    * other methods, it will raise a `NoLongerValidSecret` exception.
    */
  def destroy(): Unit

  /** Check if the secret is destroyed
    *
    * @return
    *   `true` if the secret is destroyed, `false` otherwise
    */
  def isDestroyed: Boolean

  /** Calculate the non-deterministic hash code for this Secret.
    *
    * This hash code is NOT the hash code of the original value. It is the hash code of the obfuscated value.
    *
    * Since the obfuscated value is based on a random key, the hash code will be different every time. This function is not deterministic.
    *
    * @return
    *   the hash code of this secret. If the secret is destroyed it will return `-1`.
    */
  def hashCode(): Int

  // ------------------------------------------------------------------
  /** Avoid this method if possible. Unsafely apply `f` with the de-obfuscated value WITHOUT destroying it.
    *
    * If the secret is destroyed it will raise a `NoLongerValidSecret` exception.
    *
    * Throws `SecretNoLongerValid` if the secret is destroyed
    */
  final def unsafeUse[U](f: T => U): U =
    use[Either[SecretNoLongerValid, *], U](f).fold(throw _, identity)

  /** Apply `f` with the de-obfuscated value WITHOUT destroying it.
    *
    * If the secret is destroyed it will raise a `NoLongerValidSecret` exception.
    *
    * Once the secret is destroyed it can't be used anymore. If you try to use it using `use`, `useAndDestroy`, `evalUse`, `evalUseAndDestroy` and
    * other methods, it will raise a `NoLongerValidSecret` exception.
    */
  final def use[F[_]: MonadSecretError, U](f: T => U): F[U] =
    evalUse[F, U](f.andThen(_.pure[F]))

  /** Alias for `use` with `Either[Throwable, *]` */
  final def useE[U](f: T => U): Either[SecretNoLongerValid, U] =
    use[Either[SecretNoLongerValid, *], U](f)

  /** Apply `f` with the de-obfuscated value and then destroy the secret value by invoking `destroy` method.
    *
    * Once the secret is destroyed it can't be used anymore. If you try to use it using `use`, `useAndDestroy`, `evalUse`, `evalUseAndDestroy` and
    * other methods, it will raise a `NoLongerValidSecret` exception.
    */
  final def useAndDestroy[F[_]: MonadSecretError, U](f: T => U): F[U] =
    evalUseAndDestroy[F, U](f.andThen(_.pure[F]))

  /** Alias for `useAndDestroy` with `Either[Throwable, *]` */
  final def useAndDestroyE[U](f: T => U): Either[SecretNoLongerValid, U] =
    useAndDestroy[Either[SecretNoLongerValid, *], U](f)

  /** Apply `f` with the de-obfuscated value and then destroy the secret value by invoking `destroy` method.
    *
    * Once the secret is destroyed it can't be used anymore. If you try to use it using `use`, `useAndDestroy`, `evalUse`, `evalUseAndDestroy` and
    * other methods, it will raise a `NoLongerValidSecret` exception.
    */
  final def evalUseAndDestroy[F[_]: MonadSecretError, U](f: T => F[U]): F[U] =
    evalUse(f).map { u =>
      destroy(); u
    }

  /** Alias for `destroy` */
  final override def close(): Unit = destroy()

  /** Safely compare this secret with the provided `Secret`.
    *
    * @return
    *   `true` if the secrets are equal, `false` if they are not equal or if one of the secret is destroyed
    */
  final def isEquals(that: Secret[T]): Boolean =
    evalUse[Try, Boolean](value => that.use[Try, Boolean](_ == value)).getOrElse(false)

  /** Always returns `false`, use `isEqual` instead */
  final override def equals(obj: Any): Boolean = false

  /** @return
    *   always returns a static place holder string "** SECRET **" to avoid leaking information
    */
  final override def toString: String = "** SECRET **"

object Secret extends SecretInstances with DefaultObfuscationStrategyInstances:
  def apply[T: ObfuscationStrategy](value: T): Secret[T] =
    new Secret[T] {

      private var bufferTuple: KeyValueBuffer = ObfuscationStrategy[T].obfuscator(value)

      override def evalUse[F[_]: MonadSecretError, U](f: T => F[U]): F[U] =
        if (isDestroyed)
          summon[MonadSecretError[F]].raiseError(SecretNoLongerValid())
        else
          f(ObfuscationStrategy[T].deObfuscator(bufferTuple))

      override def destroy(): Unit =
        bufferTuple.destroy()
        bufferTuple = null

      override def isDestroyed: Boolean =
        bufferTuple == null

      // noinspection HashCodeUsesVar
      override def hashCode(): Int =
        if (isDestroyed) -1 else bufferTuple.obfuscatedHashCode
    }

private[secret] sealed trait SecretInstances:

  given [T]: Hashing[Secret[T]] =
    Hashing.fromFunction(_.hashCode())

  given [T]: Eq[Secret[T]] =
    Eq.fromUniversalEquals

  given [T]: Show[Secret[T]] =
    Show.fromToString
