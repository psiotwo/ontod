package com.github.psiotwo.ontod

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl
import java.io.File

class GenerateOBOUpdateScript {
    /**
     * Generates an OWLOntology in the form of an OBO update script
     *
     * @see generate
     */
    fun generate(original: File, update: File, ontologyIri: String, outputFile: File) {
        val originalO = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(original)
        val updateO = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(update)
        val merged = generate(originalO, updateO, IRI.create(ontologyIri))
        merged.saveOntology(outputFile.outputStream())
    }

    /**
    }
     * Generates an OWLOntology in the form of a change ontology (C), simulating how to go from original (O) to update (U) in the following manner:
     * - C contains all axioms from U that are not in O
     * - C contains all axioms from O that are not in U, but marked as deprecated
     *
     * Then, going from O to U could be simulated as deleting deprecated axioms from union(O,C)
     *
     * This is useful for example when resolving unsatisfiable classes in debug files of ROBOT OBO tool. One can take a debug file,
     * fix unsatisfiable classes and use this tool to generate the fixes needed to be applied to the original ontology.
     *
     * @param original - an baseline/original ontology
     * @param update - an ontology after some (manual) curation
     * @param ontologyIri - IRI of the resulting ontology
     *
     * @return a change ontology as described above.
     */
    fun generate(original: OWLOntology, update: OWLOntology, ontologyIri: IRI): OWLOntology {
        val f = OWLManager.getOWLDataFactory()
        with(OWLManager.createOWLOntologyManager()) {
            val toAdd = update.axioms - original.axioms
            val toRemove = original.axioms - update.axioms
            val deprecated = OWLAnnotationPropertyImpl(OWLRDFVocabulary.OWL_DEPRECATED.iri)
            val annotation = f.getOWLAnnotation(deprecated, f.getOWLLiteral(true))
            val merged = createOntology(ontologyIri)
            merged.addAxioms(toAdd)
            merged.addAxioms(toRemove.map {
                it.getAnnotatedAxiom(it.javaClass, setOf(annotation).stream())
            })
            return merged
        }
    }
}