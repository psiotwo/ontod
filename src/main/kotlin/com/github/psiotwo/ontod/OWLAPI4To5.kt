package com.github.psiotwo.ontod

import org.semanticweb.owlapi.model.*
import java.util.stream.Stream
import javax.annotation.Nonnull

fun OWLAxiom.getAnnotatedAxiom(javaClass: Any, @Nonnull annotations: Stream<OWLAnnotation>): OWLAxiom =
    this.getAnnotatedAxiom(annotations.toList().toSet())

fun OWLAxiom.getAnnotatedAxiom(javaClass: Any, @Nonnull annotations: List<OWLAnnotation>): OWLAxiom =
    this.getAnnotatedAxiom(annotations.toSet())

fun OWLOntology.addAxioms(axioms: List<OWLAxiom>) {
    this.owlOntologyManager.addAxioms(this, axioms.toSet())
}

fun OWLOntology.addAxioms(axioms: Set<OWLAxiom>) {
    this.owlOntologyManager.addAxioms(this, axioms)
}

fun OWLDataFactory.getOWLClass(s: String): OWLClass {
    return this.getOWLClass(IRI.create(s))
}

fun OWLDataFactory.getOWLAnnotationProperty(s: String): OWLAnnotationProperty {
    return this.getOWLAnnotationProperty(IRI.create(s))
}

fun OWLDataFactory.getOWLNamedIndividual(s: String): OWLNamedIndividual {
    return this.getOWLNamedIndividual(IRI.create(s))
}

fun OWLClassAssertionAxiom.getAnnotatedAxiom(map: List<OWLAnnotation>): OWLAxiom {
    return this.getAnnotatedAxiom(map.toSet())
}
