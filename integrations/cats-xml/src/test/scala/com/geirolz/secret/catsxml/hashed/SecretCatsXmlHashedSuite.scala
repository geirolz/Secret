package com.geirolz.secret.catsxml.hashed

import cats.xml.codec.Decoder.Result
import cats.xml.syntax.*
import cats.xml.{Xml, XmlData}
import com.geirolz.secret.{secretTag, OneShotSecret, Secret}

class SecretCatsXmlHashedSuite extends munit.FunSuite:

  test("Secret should be decoded from xml") {
    val xml: XmlData.XmlString         = Xml.string("secret_value")
    val result: Result[Secret[String]] = xml.as[Secret[String]]

    result.toOption.get.euseAndDestroy(v => assert(v == "secret_value"))
  }

  test("OneShotSecret should be decoded from xml") {
    val xml: XmlData.XmlString = Xml.string("secret_value")
    val result                 = xml.as[OneShotSecret[String]]

    result.toOption.get.euseAndDestroy(v => assert(v == "secret_value"))
  }

  test("Secret should be encoded to json") {
    val secret: Secret[String] = Secret("secret_value")
    val result: Xml            = secret.toXml

    assert(result == Xml.string(secret.hashed))
  }

  test("OneShotSecret should be encoded to json") {
    val secret: OneShotSecret[String] = OneShotSecret("secret_value")
    val result: Xml                   = secret.toXml

    assert(result == Xml.string(secret.hashed))
  }
