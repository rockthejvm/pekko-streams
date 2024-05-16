package part3_graphs

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.{ActorMaterializer, BidiShape, ClosedShape}
import org.apache.pekko.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}

object BidirectionalFlows extends App {

  implicit val system: ActorSystem = ActorSystem("BidirectionalFlows")
  // the ActorSystem also acts as an ActorMaterializer for stream components

  /*
    Example: cryptography
   */
  def encrypt(n: Int)(string: String) = string.map(c => (c + n).toChar)
  def decrypt(n: Int)(string: String) = string.map(c => (c - n).toChar)

  // bidiFlow
  val bidiCryptoStaticGraph = GraphDSL.create() { implicit builder =>
    val encryptionFlowShape = builder.add(Flow[String].map(encrypt(3)))
    val decryptionFlowShape = builder.add(Flow[String].map(decrypt(3)))

    //    BidiShape(encryptionFlowShape.in, encryptionFlowShape.out, decryptionFlowShape.in, decryptionFlowShape.out)
    BidiShape.fromFlows(encryptionFlowShape, decryptionFlowShape)
  }

  val unencryptedStrings = List("Pekko", "is", "awesome", "testing", "bidirectional", "flows")
  val unencryptedSource = Source(unencryptedStrings)
  val encryptedSource = Source(unencryptedStrings.map(encrypt(3)))

  val cryptoBidiGraph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val unencryptedSourceShape = builder.add(unencryptedSource)
      val encryptedSourceShape = builder.add(encryptedSource)
      val bidi = builder.add(bidiCryptoStaticGraph)
      val encryptedSinkShape = builder.add(Sink.foreach[String](string => println(s"Encrypted: $string")))
      val decryptedSinkShape = builder.add(Sink.foreach[String](string => println(s"Decrypted: $string")))

      unencryptedSourceShape  ~> bidi.in1   ;   bidi.out1 ~> encryptedSinkShape
      decryptedSinkShape      <~ bidi.out2  ;   bidi.in2  <~ encryptedSourceShape

      ClosedShape
    }
  )

  cryptoBidiGraph.run()

  /*
    - encrypting/decrypting
    - encoding/decoding
    - serializing/deserializing
   */
}
