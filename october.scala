package edu.classics.furman.cite

import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import scala.util.matching.Regex
import java.io._

/** Methods for creating OHCO2 texts from flst files */
class October( val urnBase: CtsUrn ) {
  override def toString:String = s"October: ${urnBase}"

  case class IndexedLine( text: String, index: Int)
  case class TextSection( title: String, firstIndex: Int, level: Int)
  case class IndexedNode( node: CitableNode, index: Int)

  def makeIndexedLines( lines: Vector[String]): Vector[IndexedLine] = {
    lines.zipWithIndex.map( ln => IndexedLine( text = ln._1, index = ln._2 ) )
  }

  def getSections( lines: Vector[IndexedLine], level: Int, sectionFilter: IndexedLine => Boolean, titleFinder: (IndexedLine, Int) => String ): Vector[TextSection] = {
    val filteredLines: Vector[IndexedLine] = lines.filter( l => { sectionFilter(l) })
    filteredLines.zipWithIndex.map( zl => {
      val title: String = titleFinder( zl._1, zl._2 )
      val thisLevel: Int = level
      val firstIndex: Int = zl._1.index
      TextSection( title, firstIndex, thisLevel )
    })
  }

  // Assumes the the IndexedNodes passed in lack a final field in the URN passage
  def idLeafNodes( lines: Vector[IndexedNode], idMaker: ( IndexedNode, Vector[IndexedNode] ) => CtsUrn ): Vector[IndexedNode] = {
    lines.map( l => {
      val u: CtsUrn = idMaker(l, lines)
      val i: Int = l.index
      val t: String = l.node.text 
      val n = CitableNode( u, t)
      IndexedNode( n, i)
    })

  }

  def leafNodeBySeq( line: IndexedNode, allLines: Vector[IndexedNode]): CtsUrn = {
    val baseUrn: CtsUrn = line.node.urn     
    val oneSection: Vector[IndexedNode] = allLines.filter( ln => {
      ln.node.urn == baseUrn
    })
    val indexVec: Vector[Int] = oneSection.map(_.index)
    val thisIndex: Int = indexVec.indexOf( line.index ) + 1
    val newPassage: String = {
      val basePassage: String = baseUrn.passageComponent  
      basePassage match {
        case "" => thisIndex.toString
        case _ => s"${basePassage}.${thisIndex}"
      }
    }
    baseUrn.addPassage(newPassage)
  }

  def sectionForLine( lineIndex: Int, sections: Vector[TextSection]): String = {
    val possibleSections: Vector[TextSection] = sections.filter( s => {
      ( s.firstIndex <= lineIndex )
    })
    if (possibleSections.size == 0) ""
    else possibleSections.last.title
  }

  def filterOut( iNodes: Vector[IndexedNode], omit: Vector[TextSection]) = {
    val justIndices: Vector[Int] = omit.map(_.firstIndex) 
    iNodes.filterNot( in => {
      justIndices.contains( in.index )
    })

  }

  def appendCitation( n: IndexedNode, sections: Vector[TextSection] ): IndexedNode = {
      val value: String = sectionForLine( n.index, sections )
      val newUrn: CtsUrn = {
        val base: CtsUrn = n.node.urn.dropPassage
        val oldPassage: String = n.node.urn.passageComponent
        val newPassage: String = oldPassage match {
          case "" => value
          case _ => {
            if (value == "") ""
            else s"${oldPassage}.${value}"
          }
        }
        base.addPassage(newPassage)
      }
      val i: Int = n.index
      val u: CtsUrn = newUrn
      val t: String = n.node.text
      val newNode = CitableNode( u, t)
      IndexedNode(newNode, i)
  }


  /* 
    ------------------
    Utilities
    ------------------
  */

  /** 
   * Loads a file into a Vector of Strings
   * @param fp The absolute or relative path to the file
   */
  def loadFile( fp: String ): Vector[String] = {
    Source.fromFile(fp).getLines.toVector
  }

  /** 
   * Saves a string to a file
   * @param s The string to save
   * @param filePath The path, ending with '/'
   * @param fileName The name of the file
   */  
   def saveString(s:String, filePath:String = "texts/", fileName:String = "temp.txt"):Unit = {
    val pw = new PrintWriter(new File(filePath + fileName))
    pw.append(s)
    pw.close
  }

}
object October {
  def apply( base: CtsUrn) = {
    val oct = new October(base)
    oct
  }
  
}
  