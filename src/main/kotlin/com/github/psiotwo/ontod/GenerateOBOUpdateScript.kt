package com.github.psiotwo.ontod

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl
import java.io.File

class GenerateOBOUpdateScript {
    /**
     * Generates an OWLOntology in the form of an OBO update script
     */
    fun generate(original: File, update: File, ontologyIri: String, outputFile: File) {
        with(OWLManager.createOWLOntologyManager()) {
            val originalO = loadOntologyFromOntologyDocument(original)
            val updateO = loadOntologyFromOntologyDocument(update)
            val merged = generate(originalO, updateO, IRI.create(ontologyIri))
            merged.saveOntology(outputFile.outputStream())
        }
    }

    /**
        }
     * Generates an OWLOntology in the form of an OBO update script
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