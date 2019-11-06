import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import java.io._
import scala.annotation.tailrec
import java.util.Calendar

:load utilities.sc

// We're keeping the ' char and the hyphen!
//    (That's why this is different from the 'punctution' value in utilities.sc)
val removePunctuation: String = """[“”“‘()‘’\[\]_·…⸁.,:; "?·!⸂⸃–—-]"""

val spellCheckSplitters: String =  """[()\[\]·⸁.,; "?·!–—⸂⸃-]"""

val lib: CiteLibrary = loadLibrary("text/arist_politics.cex")

val standardDict: Vector[String] = loadFile("text/SCOWL-wl/words.txt")
val userDict: Vector[String] = loadFile("text/userDictionary.txt")
val words: Vector[String] = standardDict ++ userDict

val tr: TextRepository = lib.textRepository.get

val cat: Catalog = tr.catalog

val corp: Corpus = tr.corpus

/* Let's make a *tokenized exemplar*! */

val exemplarLabel: String = "spellcheck"

/* We do this by mapping the .nodes of a Corpus
		1. For each .node in the Corpus…
		2. Split the .text into tokens
		3. Attach an index-number to each token
		4. For each token, make a new URN that adds the index number
		5. With a URN and a Text (the token) you can make a CitableNode
		6. Return that Citable Node
		(We will drop any tokens that consist only of punctuation, and remove punctuation from the others.)
*/
val tokenizedVector: Vector[CitableNode] = corp.nodes.map( n => {
	// Grab the original URN
	val urnBase: CtsUrn = n.urn
	// Get its citation-component
	val citationBase: String = urnBase.passageComponent
	// Split up the text into tokens
	val tokenizedText: Vector[String] = splitWithSplitter(n.text, spellCheckSplitters).filter( _ != " ")
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
}).flatten.filter( n => {
	((n.text.size > 0) && (n.text.replaceAll(punctuation,"").size > 0))
}).map( n => {
	val nt: String = n.text.replaceAll("’","'").replaceAll(removePunctuation,"")
	CitableNode( n.urn, nt)
})

/* We mapped a Vector[CitableNode] and each node became a Vector[CitableNode].
   So we have a Vector[Vector[CitableNode]], which we need to "flatten" into
   a Vector[CitableNode]. 
*/

// We make a Corpus out of our new Vector[CitableNode]
val tokenCorp: Corpus = Corpus(tokenizedVector)

/* We don't want to spell-check the same word again and again.
	 But we _do_ want to know the citations for each instance of a word.
*/

val wordIndex: Vector[ (String, Vector[CtsUrn]) ] = {
	tokenCorp.nodes.groupBy( n => n.text ).toVector.map( m => {
		val word: String = m._1
		val urnIndex: Vector[CtsUrn] = m._2.map(_.urn)
		(word, urnIndex )
	}).sortBy( m => m._2.size).reverse
}

/*  Spell-Check!
		Here's a problem… capitalized normal words.
		The word-list has "the" but not "The".
		We don't want to lower-case all words, because it has "Paris" but
		not "paris".			
		So we check for both…
*/
val badWords: Vector[ (String, Vector[CtsUrn]) ] = {
	wordIndex.filter( wi => {

		val hasRegular: Boolean = words.contains(wi._1) 
		val hasLowerCase: Boolean = words.contains(wi._1.toLowerCase) 
		// If either (`or`) is true, say "true"
		val inDictionary: Boolean = hasRegular | hasLowerCase 
		// But remember… we want _bad_ words!
		inDictionary == false
	})
}


/* Observed problems!
	 - we need to filter out tokens that are all just arabic numbers.
	 - we need to split hyphenated words, and check both sides.
	 - perhaps we need to find a list of geographic names?
*/


