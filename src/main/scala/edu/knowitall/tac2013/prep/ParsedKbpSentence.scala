package edu.knowitall.tac2013.prep

import edu.knowitall.tool.chunk.ChunkedToken
import edu.knowitall.tool.chunk.Chunker
import edu.knowitall.tool.tokenize.Tokenizer
import KbpSentence.tabRegex
import scala.Array.canBuildFrom
import edu.knowitall.tool.parse.graph.DependencyGraph


case class ParsedKbpSentence(
    val docId: String,
    val sentNum: Int,
    val startOffset: Int,
    val chunks: Seq[String],
    val dgraph: DependencyGraph) {
  
  import ParsedKbpSentence.wsSplit
  
  require(chunks.size == dgraph.nodes.size)
  
  lazy val startOffsetInt = startOffset.toInt
  
  def chunkedTokens = {
    
    val postaggedTokens = dgraph.nodes.toSeq
    postaggedTokens.zip(chunks) map { case (postaggedToken, chunk) =>
      new ChunkedToken(postaggedToken, chunk)
    }
  }
}
    
object ParsedKbpSentence {
  
  private val wsSplit = "\\s+".r
  
  val NUM_FIELDS = 5
  
  import KbpSentence.tabRegex
  
  def read(pickle: String): Option[ParsedKbpSentence] = read(tabRegex.split(pickle))
  
  def read(split: Array[String]): Option[ParsedKbpSentence] = {
    split match {
      case Array(docId, sentNum, startOffset, chunks, dgraph, _*) => 
        Some(ParsedKbpSentence(docId, sentNum.toInt, startOffset.toInt, wsSplit.split(chunks), DependencyGraph.deserialize(dgraph)))
      case _ => {
        System.err.println("Error reading ParsedKbpSentence: %s".format(split.mkString("\t")))
        None
      }
    }
  }
  
  def write(s: ParsedKbpSentence): String = {
    Seq(
      s.docId,
      s.sentNum.toString,
      s.startOffset.toString,
      s.chunks.mkString(" "),
      s.dgraph.serialize
    ).map(_.replaceAll("\t", " ")).mkString("\t")
  }
}