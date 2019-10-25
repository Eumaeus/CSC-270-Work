import scala.io.Source

:load utilities.sc

val lib = loadLibrary("../repo_will/text/lesMiserables_fra.cex")

val corpus = lib.textRepository.get.corpus

val tokenizedCorpus: Vector[CitableNode] = corp.nodes.map( n => {
	val urnBase: CtsUrn = n.urn
	val citationBase: String = urnBase.passageComponent
	val tokenizedText: Vector[String] = splitWithSplitter(n.text)
	val tokens: Vector[CitableNode] = tokenizedText.zipWithIndex.map( z => {
		val citation: String = s"${citationBase}.${z._2 + 1}"
		val urn: CtsUrn = urnBase.addPassage(citation)
		val passage: String = z._1
		CitableNode(urn, passage)
	})
	tokens
}).flatten