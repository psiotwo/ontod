package com.github.psiotwo.ontod

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLAxiom
import java.io.File


class DiffTest {

    private val m = OWLManager.createOWLOntologyManager()

    @Test
    fun testGeneratesEmptyOntologyWhenBothOntologiesAreEmpty() {
        val original = m.createOntology()
        val update = m.createOntology()

        val diff = Diff(original, update)

        Assertions.assertTrue(diff.clean)
        Assertions.assertTrue(diff.empty)
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

        val diff = Diff(original, update)

        Assertions.assertTrue(diff.clean)
        Assertions.assertEquals(emptySet<OWLAxiom>(), diff.inLeftButNotRight.toSet())
        Assertions.assertEquals(update.axioms, diff.inRightButNotLeft.toSet())
    }

    @Test
    fun testGeneratesCorrectlySingleAxiomDeprecation() {
        val original = m.createOntology().apply {
            addAxioms(axiom {
                instanceOf("a3", "c")
            }.build())
        }

        val update = m.createOntology()

        val diff = Diff(original, update)

        Assertions.assertTrue(diff.clean)
        Assertions.assertEquals(emptySet<OWLAxiom>(), diff.inRightButNotLeft.toSet())
        Assertions.assertEquals(original.axioms, diff.inLeftButNotRight.toSet())
    }

    @Test
    @Disabled
    fun testHuge() {
        println("Diff started.")
        val file1 = "original.owl"
        val file2 = "update.owl"
        val original =
            OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(
                File(file1))
        println("Original loaded.")
        val update =
            OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(
                File(file2))
        println("Update loaded.")
        val diff = Diff(original, update)
        println("Diff executed.")
        File("test.md").writer().use {
            diff.markdownFramed(it)
        }
    }
}