import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import java.io._
import scala.annotation.tailrec
import java.util.Calendar

:load utilities.sc

val lib: CiteLibrary = loadLibrary("text/arist_politics.cex")

val tr: TextRepository = lib.textRepository.get

val cat: Catalog = tr.catalog

val corp: Corpus = tr.corpus

/* Let's make a *tokenized exemplar*! */

val exemplarLabel: String = "wt"

/* We do this by mapping the .nodes of a Corpus
		1. For each .node in the Corpus…
		2. Split the .text into tokens
		3. Attach an index-number to each token
		4. For each token, make a new URN that adds the index number
		5. With a URN and a Text (the token) you can make a CitableNode
		6. Return that Citable Node
*/
val tokenizedVector: Vector[CitableNode] = corp.nodes.map( n => {
	// Grab the original URN
	val urnBase: CtsUrn = n.urn
	// Get its citation-component
	val citationBase: String = urnBase.passageComponent
	// Split up the text into tokens
	val tokenizedText: Vector[String] = splitWithSplitter(n.text).filter( _ != " ")
	// Map these tokens into Citable Nodes
	val tokens: Vector[CitableNode] = tokenizedText.zipWithIndex.map( z => {
		// By adding to the citation-component
		val citation: String = s"${citationBase}.${z._2 + 1}"
		// And creating a new URN
		val urn: CtsUrn = urnBase.addExemplar(exemplarLabel).addPassage(citation)
		// And getting the text of the new token-citable-node
		val passage: String = z._1
		// And making a CitableNode out of URN + Text
		CitableNode(urn, passage)
	})
	tokens
}).flatten

/* We mapped a Vector[CitableNode] and each node became a Vector[CitableNode].
   So we have a Vector[Vector[CitableNode]], which we need to "flatten" into
   a Vector[CitableNode]. 
*/

// We make a Corpus out of our new Vector[CitableNode]
val tokenCorp: Corpus = Corpus(tokenizedVector)

/* Let's test it a little! */
val origSize: Int = corp.size
val tokSize: Int = {
	tokenCorp.urns.map( _.collapsePassageBy(1) ).distinct.size
}
assert( origSize == tokSize )

/* Citation-Aware N-Grams */

// Let's lose all the punctuation-tokens for this analysis…
val noPuncCorpus: Corpus = {
	// Remove tokens that consist only of punctuation
	val nodes: Vector[CitableNode] = {
		tokenCorp.nodes.filter( n => {
			n.text.replaceAll(punctuation,"").size > 0
		})
	}
	// replace punctuation within nodes
	val lcNodes: Vector[CitableNode] = nodes.map( n => {
		CitableNode( n.urn, n.text.toLowerCase.replaceAll(punctuation,"") )
	})
	// return a new, punctuation-free corpus
	Corpus( lcNodes )
}

// Using .sliding, let's get some 3-grams…
def makeNGramTuples( n: Int, c: Corpus): Vector[(CtsUrn, String)] = {
	// Using .sliding, let's get all possible combinations of N tokens
	val slid: Vector[Vector[CitableNode]] = {
		c.nodes.sliding(n,1).toVector
	}
	/* Map this into a Vector[ (CtsUrn, String)]
		 return this value
	*/
	slid.map( s => {
		val newUrn: CtsUrn = s.head.urn.addPassage(
			s"${s.head.urn.passageComponent}-${s.last.urn.passageComponent}"
		)
		val newText: String = s.map( _.text ).mkString(" ")
		(newUrn, newText)
	})
}

def makeNGramHisto( tups: Vector[(CtsUrn, String)]): Vector[(String, Int)] = {
	tups.map( _._2 ).view.groupBy( n => n).toVector.map( n => {
		( n._1, n._2.size )
	}).sortBy( _._2 ).reverse
}

def urnsForNGram( s:String, ngt: Vector[ (CtsUrn, String)] ): Set[CtsUrn] = {
	ngt.filter( _._2 == s ).map(_._1).toSet
}

val ngt = makeNGramTuples(3, noPuncCorpus)
val ngh = makeNGramHisto(ngt)

/* Given a histogram, return the elements whose frequency sums to a given
   percentage of the whole.
*/
def takePercent( histo: Vector[(String, Int)], targetPercent: Int): Vector[(String, Int)] = {
	@tailrec def sumTakePercent(totalInstances: BigInt, h: Vector[(String, Int)], justNumbers: Vector[Int]): Vector[(String, Int)] = {
		val sum: BigInt = justNumbers.sum
		val currentPercent: Double = (sum.toDouble / totalInstances.toDouble) * 100
		if ( currentPercent <= targetPercent ) {
			h.sortBy(_._2).reverse
		} else {
			sumTakePercent( totalInstances, h.tail, justNumbers.tail )
		}
	}
	val t: BigInt = histo.map(_._2).sum
	val h: Vector[(String, Int)] = histo.sortBy(_._2) // we want _ascending_ order!
	val n: Vector[Int] = h.map(_._2) // we don't want to re-map the whole histo each time!
	sumTakePercent( t, h, n) 
}
