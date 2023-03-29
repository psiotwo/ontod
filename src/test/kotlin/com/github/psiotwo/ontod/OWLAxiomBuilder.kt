package com.github.psiotwo.ontod

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLDataFactory

val f: OWLDataFactory = OWLManager.createOWLOntologyManager().owlDataFactory

var prefix = "http://xxx.yyy/"

class OWLAxiomBuilder {

    var a: MutableSet<OWLAxiom> = mutableSetOf()

    fun build(): Set<OWLAxiom> {
        return a
    }

    fun instanceOf(instanceName: String, clazzName: String, vararg stringAnnotations: Pair<Any, Any>) {
        a.add(f.getOWLClassAssertionAxiom(
            f.getOWLClass(prefix + clazzName),
            f.getOWLNamedIndividual(prefix + instanceName)
        ).getAnnotatedAxiom(
            stringAnnotations.map { (p, o) ->
                f.getOWLAnnotation(
                    when (p) {
                        is IRI -> f.getOWLAnnotationProperty(p)
                        else -> f.getOWLAnnotationProperty(prefix + p)
                    },
                    when (o) {
                        is Boolean -> f.getOWLLiteral(o)
                        else -> f.getOWLLiteral(o.toString())
                    }
                )
            }
        ))
    }
}

fun axiom(initializer: OWLAxiomBuilder.() -> Unit): OWLAxiomBuilder {
    return OWLAxiomBuilder().apply(initializer)
}