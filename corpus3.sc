import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import java.io._
import scala.annotation.tailrec
import java.util.Calendar

:load utilities.sc

val lib: CiteLibrary = loadLibrary("text/myText.cex")

val tr: TextRepository = lib.textRepository.get

val cat: Catalog = tr.catalog

val corp: Corpus = tr.corpus

/* Let's make a *tokenized exemplar*! */

val exemplarLabel: String = "wt"

val tokenizedVector: Vector[CitableNode] = corp.nodes.map( n => {
	val urnBase: CtsUrn = n.urn
	val citationBase: String = urnBase.passageComponent
	val tokenizedText: Vector[String] = splitWithSplitter(n.text)
	val tokens: Vector[CitableNode] = tokenizedText.zipWithIndex.map( z => {
		val citation: String = s"${citationBase}.${z._2 + 1}"
		val urn: CtsUrn = urnBase.addExemplar(exemplarLabel).addPassage(citation)
		val passage: String = z._1
		CitableNode(urn, passage)
	})
	tokens
}).flatten

val tokenCorp: Corpus = Corpus(tokenizedVector)

// test it!
val origSize: Int = corp.size
val tokSize: Int = {
	tokenCorp.urns.map( _.collapsePassageBy(1) ).distinct.size
}
assert( origSize == tokSize )

/* Citation-Aware N-Grams */


