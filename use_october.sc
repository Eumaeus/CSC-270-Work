import edu.classics.furman.cite._
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import scala.util.matching.Regex


val urnBase: CtsUrn = CtsUrn("urn:cts:latinLit:phi1212.phi002.chin:")

val oct = October(urnBase)

/** 
 * Filter out unwanted lines of text.
 * This will be specific to any text.
 **/
val rawLines: Vector[String] = oct.loadFile("text/Apuleius/apul_all.txt").filter( ln => {
	val firstFive: String = ln.take(5)	
	firstFive match {
		case "<html" => false
		case "<body" => false
		case "<head" => false
		case "</htm" => false
		case "</bod" => false
		case _ => true
	}
}).filter(_.size > 0)

/** 
 * Functions for finding and naming sections headings.
 * Specific to each text! 
 **/

def level1Filter( ln: oct.IndexedLine ): Boolean = {
	ln.text.contains("""<i>The Golden Ass</i>""")
}

def level1Title( thisLine: oct.IndexedLine, thisIndex: Int ): String = {
	(thisIndex + 1).toString
}

def level2Filter( ln: oct.IndexedLine ): Boolean = {
	val matcher = new Regex("""^\[([0-9]+)\]""")	
	val matchVec = matcher.findAllIn(ln.text).matchData.toVector
	matchVec.size > 0
}

def level2Title( thisLine: oct.IndexedLine, thisIndex: Int): String = {
	val s: String = thisLine.text
	val matcher = new Regex("""^\[([0-9]+)\]""")
	val matchVec = matcher.findAllIn(s).matchData.toVector
	matchVec size match {
		case n if (n > 0) => {
			matchVec.head.group(1).toString
		}
		case _ => s"BadMatch_${thisLine.index}"
	}
}

/**
 * Processing a text.
 * These depend on "oct", defined above.
 **/

val iLines = oct.makeIndexedLines(rawLines)

val level1Sections: Vector[oct.TextSection] = {
	oct.getSections( iLines, 1, level1Filter, level1Title )
}
val level2Sections: Vector[oct.TextSection] = {
	oct.getSections( iLines, 2, level2Filter, level2Title )
}

val baseNodes: Vector[oct.IndexedNode] = {
	iLines.map( il => {
		val n: CitableNode = CitableNode( urnBase, il.text)
		val i: Int = il.index
		oct.IndexedNode(n, i)
	})
}

val level1Nodes: Vector[oct.IndexedNode] = {
	baseNodes.map( bn => {
		oct.appendCitation( bn, level1Sections )
	})
}

val level2Nodes: Vector[oct.IndexedNode] = {
	oct.filterOut(level1Nodes, level1Sections).map( bn => {
		oct.appendCitation( bn, level2Sections )
	})
}

val finalNodes: Vector[CitableNode] = {
	oct.idLeafNodes( level2Nodes, oct.leafNodeBySeq).map( _.node )
}

