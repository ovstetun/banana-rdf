package org.w3.rdf

import org.w3.rdf._
import org.w3.rdf.diesel._
import org.scalatest._
import org.scalatest.matchers._
import java.io.File

abstract class StoreTest[Rdf <: RDF](
  ops: RDFOperations[Rdf],
  dsl: Diesel[Rdf],
  rdfStore: RDFStore[Rdf],
  reader: RDFReader[Rdf, RDFXML],
  iso: GraphIsomorphism[Rdf]
  // queryBuilder: SPARQLQueryBuilder[Rdf, Sparql],
  // queryExecution: SPARQLGraphQueryExecution[Rdf, Sparql]
) extends WordSpec with MustMatchers {

//  val projections = RDFNodeProjections(ops)

  import rdfStore._
  import iso._
  import ops._
  import dsl._

  val store: Rdf#Store
  val foaf = FOAFPrefix(ops)

  val graph: Rdf#Graph = (
    bnode("betehess")
      -- foaf.name ->- "Alexandre".lang("fr")
      -- foaf.title ->- "Mr"
  ).graph

  val graph2: Rdf#Graph = (
    bnode("betehess")
      -- foaf.name ->- "Alexandre".lang("fr")
      -- foaf.knows ->- (
        uri("http://bblfish.net/#hjs")
          -- foaf.name ->- "Henry Story"
          -- foaf.currentProject ->- uri("http://webid.info/")
      )
  ).graph

  "getNamedGraph should retrieve the graph added with addNamedGraph" in {
    addNamedGraph(store, IRI("http://example.com/graph"), graph)
    addNamedGraph(store, IRI("http://example.com/graph2"), graph2)
    val retrievedGraph = getNamedGraph(store, IRI("http://example.com/graph"))
    val retrievedGraph2 = getNamedGraph(store, IRI("http://example.com/graph2"))
    assert(graph isIsomorphicWith retrievedGraph)
    assert(graph2 isIsomorphicWith retrievedGraph2)
  }

  

  // "simple scenario" in {
  //   val base = "http://example.com"
  //   val trs = reader.read(new File("rdf-test-suite/src/main/resources/new-tr.rdf"), base).fold(t => sys.error(t.toString), s => s)
  //   val editors = reader.read(new File("rdf-test-suite/src/main/resources/known-tr-editors.rdf"), base) getOrElse sys.error("")
  //   addNamedGraph(store, IRI("http://example.com/new-tr.rdf"), trs)
  //   addNamedGraph(store, IRI("http://example.com/known-tr-editors.rdf"), editors)
  //   val getTrs = getNamedGraph(store, IRI("http://example.com/new-tr.rdf"))
  //   println(trs.size)
  //   println(getTrs.size)
  //   assert(trs isIsomorphicWith getTrs)
  // }

}
