# OntoD

OntoD currently contains an operation [GenerateOBOUpdateScriptTest.kt](src%2Ftest%2Fkotlin%2Fcom%2Fgithub%2Fpsiotwo%2Fontod%2FGenerateOBOUpdateScriptTest.kt) that takes two revisions of the same ontology - say `original` and `update` 
and generates a new ontology containing the update operations to get from original to update:
- axiom additions - represented as normal OWL axioms
- axiom removals - represented as OWL axioms marked as `owl:deprecated`

Note that this design goes beyond the OWL specification that expects `owl:deprecated` to be only applicable for entities.

See [test resources](src%2Ftest%2Fresources) for some test cases showing how it works.
