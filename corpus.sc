import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import java.io._
import scala.annotation.tailrec

:load utilities.sc

val lib: CiteLibrary = loadLibrary("text/myText.cex")

val tr: TextRepository = lib.textRepository.get

val cat: Catalog = tr.catalog

val corp: Corpus = tr.corpus

/* CATALOG GAMES */

println(s"\nThings you can do with a catalog.\n")


println(s"Find out what texts are in the library…")
cat.texts

println()

println(s"Find out what specific versions are in the library…")
cat.versions

println()

println(s"Find out what specific *exemplars* are in the library…")
cat.exemplars

println()

println(s"Find out what notional works are in the library…")
cat.works

println()

// The catalog can give you info about a text, if you know its URN

val versionUrn: CtsUrn = cat.versions.head // get the first version in the Set of versions


println(s"Group name…")
cat.groupName(versionUrn)

println(s"Work title…")
cat.workTitle(versionUrn)


println(s"Version label…")
cat.versionLabel(versionUrn)

// Or, you can query each CatalogEntry specifically…

println(s"What a CatalogEntry looks like…")
val catEntry: CatalogEntry = cat.texts.head
catEntry

// get a description of the citation scheme for this text…

println(s"What is the citation scheme for a text?")
catEntry.citationScheme

/* CORPUS GAMES */

corp

corp.size

corp.nodes(0)

corp.nodes(1)

corp.nodes(10)

corp.nodes.last.text

// Just the URN
corp.nodes(10).urn
// Just the Text
corp.nodes(10).text

// List of all URNs
var urnList: Vector[CtsUrn] = corp.nodes.map( _.urn )
// type 'showMe(urnList)' to see the list, after you run this script


// Char histo?

val charHisto: Vector[(Char, Int)] = {
	// For each node in the Corpus, keep only the text-part (toss the URN)
	val justText: Vector[String] = corp.nodes.map( _.text )
	// Map each element of that Vector to a Vector[Char]
	val makeChars: Vector[ Vector[Char] ] = justText.map( _.toVector )
	// Nested Vectors are confusing… let's flatten it
	val flatCharVec: Vector[ Char ] = makeChars.flatten
	// You've done this before
	val grouped: Vector[ ( Char, Vector[Char] ) ] = flatCharVec.groupBy( c => c ).toVector
	// return the result of the following as the value for charHisto
	grouped.map( g => {
		( g._1, g._2.size )
	}).sortBy( _._2 ).reverse
}

// Type 'showMe(charHisto)' to see the result

/* Character validation */

// Make a vector of legit characters. Make it the easy way!
val goodChars: Vector[Char] = """ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890—abcdefghijklmnopqurstuv wxyz.,:;-?!“‘_’”()[]ëç&êÊôàïÏîÎôÔéèÉÈâ""".toVector

/* 
		We can make one Corpus out of another!
*/
val badCharCorpus: Corpus = {
	// Filter the contents of 'corp' by omitting any nodes that has _no_ bad chars
	val badNodes: Vector[CitableNode] = corp.nodes.filter( n => {
		val chars: Vector[Char] = n.text.toVector.distinct
		// return the following as the value of badNodes
		chars.diff(goodChars).size > 0
	})
	// For each of the offending nodes, make a new version that has only the bad chars
	val boiledDown: Vector[CitableNode] = badNodes.map( n => {
		val u: CtsUrn = n.urn
		val cc: Vector[Char] = n.text.toVector.distinct
		val badCharString: String = cc.diff(goodChars).mkString(" ")
		// return the following as the value of boiledDown
		CitableNode(u, badCharString)
	})
	// make a Corpus out of the Vector[CitableNode] you got in boiledDown
	Corpus(boiledDown)
}

// Type 'showMe(badCharCorpus)' to see the result

