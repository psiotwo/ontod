package com.github.psiotwo.ontod

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import kotlin.jvm.optionals.getOrNull

class GenerateOBOUpdateScriptTest {

    private val sut = GenerateOBOUpdateScript()

    private val m = OWLManager.createOWLOntologyManager()
    private val iri = IRI.create(prefix)

    @Test
    fun testGeneratesEmptyOntologyWhenBothOntologiesAreEmpty() {
        val original = m.createOntology()
        val update = m.createOntology()

        val diff = sut.generate(original, update, iri)

        Assertions.assertEquals(0, diff.axiomCount)
        Assertions.assertEquals(iri, diff.ontologyID?.ontologyIRI?.getOrNull())
    }

    @Test
    fun testGeneratesUpdateOntologyWhenOriginalIsEmpty() {
        val original = m.createOntology()
        val update = m.createOntology().apply {
            addAxioms(axiom {
                instanceOf("a1", "c")
                instanceOf("a2", "c")
            }.build())
        }
        val diff = sut.generate(original, update, iri)
        Assertions.assertEquals(update.axioms, diff.axioms)
    }

    @Test
    fun testGeneratesCorrectlySingleAxiomDeprecation() {
        val original = m.createOntology().apply {
            addAxioms(axiom {
                instanceOf("a3", "c")
            }.build())
        }

        val update = m.createOntology()

        val diff = sut.generate(original, update, iri)

        Assertions.assertEquals(axiom {
            instanceOf("a3", "c", Pair(OWLRDFVocabulary.OWL_DEPRECATED.iri, true))
        }.build(), diff.axioms)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/testCases.csv"], numLinesToSkip = 1)
    fun testMultiple(directory: String, fileExtension : String) {
        val original: OWLOntology =
            OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(javaClass.getResourceAsStream("/$directory/original.$fileExtension")!!)
        val updated: OWLOntology =
            OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(javaClass.getResourceAsStream("/$directory/updated.$fileExtension")!!)
        val updateScriptExpected: OWLOntology =
            OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(javaClass.getResourceAsStream("/$directory/update-script-expected.$fileExtension")!!)

        val updateScriptActual = sut.generate(original, updated, updateScriptExpected.ontologyID.ontologyIRI.get())

        Assertions.assertEquals(updateScriptExpected.axioms, updateScriptActual.axioms)
    }
}