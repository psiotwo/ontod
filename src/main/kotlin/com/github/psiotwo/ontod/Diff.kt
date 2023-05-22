package com.github.psiotwo.ontod

import org.protege.owl.diff.Engine
import org.protege.owl.diff.align.algorithms.*
import org.protege.owl.diff.present.algorithms.*
import org.semanticweb.owlapi.io.OWLObjectRenderer
import org.semanticweb.owlapi.io.RDFTriple
import org.semanticweb.owlapi.io.ToStringRenderer
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.util.ShortFormProvider
import org.semanticweb.owlapi.util.SimpleShortFormProvider
import java.io.Writer

class Diff(private val left: OWLOntology, private val right: OWLOntology) {

    val inLeftButNotRight: List<OWLAxiom> by lazy {
        (left.axioms - right.axioms).sorted()
    }

    val inLeftUnparsedTriplesButNotRight: List<RDFTriple> by lazy {
        (left.unparsedTriples() - right.unparsedTriples()).sorted()
    }

    val inRightButNotLeft: List<OWLAxiom> by lazy {
        (right.axioms - left.axioms).sorted()
    }

    val inRightUnparsedTriplesButNotLeft: List<RDFTriple> by lazy {
        (right.unparsedTriples() - left.unparsedTriples()).sorted()
    }

    private fun OWLOntology.unparsedTriples(): Set<RDFTriple> =
        emptySet()
//        this.format?.ontologyLoaderMetaData?.orElse(null)?.unparsedTriples?.toList()?.toSet() ?: setOf()

    val clean = inRightUnparsedTriplesButNotLeft.isEmpty() && inLeftUnparsedTriplesButNotRight.isEmpty()

    val empty = inRightButNotLeft.isEmpty() && inLeftButNotRight.isEmpty() && clean

    private val renderer: OWLObjectRenderer = ManchesterOWLSyntaxOWLObjectRendererImpl()

    private val shortFormProvider: ShortFormProvider = SimpleShortFormProvider()

    fun markdown(w: Writer) {
        ToStringRenderer.getInstance().setRenderer(renderer)
        w.write("# In left, but not right: \n### Axioms")
        renderList(inLeftButNotRight, w)
        w.write("\n### Unparsed triples:")
        renderList(inLeftUnparsedTriplesButNotRight, w)
        w.write("\n\n# In right, but not left:\n### Axioms")
        renderList(inRightButNotLeft, w)
        w.write("\n### Unparsed triples:")
        renderList(inRightUnparsedTriplesButNotLeft, w)
    }

    private fun getMainObject(a: OWLAxiom): OWLObject =
        when (a) {
            is OWLClassAssertionAxiom -> a.individual
            is OWLSubClassOfAxiom -> a.subClass
            is OWLEquivalentClassesAxiom -> a.classExpressions.minOf { it }
            is OWLObjectPropertyAssertionAxiom -> a.subject
            is OWLObjectPropertyDomainAxiom -> a.property
            is OWLObjectPropertyRangeAxiom -> a.property
            is OWLDataPropertyAssertionAxiom -> a.subject
            is OWLDataPropertyDomainAxiom -> a.property
            is OWLDataPropertyRangeAxiom -> a.property

            is OWLAnnotationAssertionAxiom -> a.subject

            else -> a
        }


    private fun <T> renderList(list: Collection<T>, writer: Writer, framed: Boolean = false) {
        if (list.isEmpty()) {
            writer.write("\n - *none*")
        } else {
            if (!framed || (list.iterator().next() !is OWLAxiom)) {
                for (s in list) {
                    writer.write("\n - ${s.toString()}")
                }
            } else {
                val c = list as Collection<OWLAxiom>
                val m = c.groupBy { getMainObject(it) }

                for (k in m.keys) {
                    writer.write("\n - $k")
                    for (v in m[k]!!) {
//                        renderer.setShortFormProvider { entity ->
//                            println("'${entity.hashCode()} of ${entity.javaClass}' : '${k.hashCode()} of ${k.javaClass}' : '${k == entity}'")
//                            if (k == entity) "" else shortFormProvider.getShortForm(entity)
//                        }
                        renderer.setShortFormProvider(object : ShortFormProvider {
                            override fun getShortForm(entity: OWLEntity): String =
                                if (k == entity) "" else shortFormProvider.getShortForm(entity)

                            override fun dispose() {
                            }
                        })
                        writer.write("\n   - $v")
                    }
                }
            }
        }
    }

    fun markdownFramed(w: Writer) {
        val e = Engine(left, right)
        e.setAlignmentAlgorithms(
            MatchById(),
            MatchSiblingsWithSimilarBrowserText(),
            MatchLoneSiblings(),
            MatchByRendering(),
            MatchByCode(),
            MatchByIdFragment(),
            SuperSubClassPinch(),
            MatchStandardVocabulary(),
            DeferDeprecationAlgorithm()
        )
        e.phase1()
        e.setPresentationAlgorithms(
            IdentifyAnnotationRefactored(),
            IdentifyChangedAnnotation(),
            IdentifyAxiomAnnotationChanged(),
            IdentifyChangedDefinition(),
            IdentifyChangedSuperclass(),
            IdentifyDeprecatedAndReplaced(),
            IdentifyDeprecatedEntity(),
            IdentifyMergedConcepts(),
            IdentifyOrphanedAnnotations(),
            IdentifyPunConversions(),
            IdentifyRenameOperation(),
            IdentifyRetiredConcepts(),
            IdentifySplitConcepts()
        )
        e.phase2()
        println(e.changes)
//        ToStringRenderer.setRenderer { renderer }
//        w.write("# In left, but  not right: \n### Axioms")
//        renderList(inLeftButNotRight, w, true)
//        w.write("\n### Unparsed triples:")
//        renderList(inLeftUnparsedTriplesButNotRight, w, true)
//        w.write("\n\n# In right, but not left:\n### Axioms")
//        renderList(inRightButNotLeft, w, true)
//        w.write("\n### Unparsed triples:")
//        renderList(inRightUnparsedTriplesButNotLeft, w, true)
    }
}