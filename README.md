# Task 1: VERIFIER
Code and do some analysis on the Readers & Writers Petri Net. Add a test to check that in no path long at most 100 states mutual
exclusion fails (no more than 1 writer, and no readers and writers together). Can you extract a small API for representing safety properties?
What other properties can be extracted? How the boundness assumption can help?

## Implementazione: 

//Vedi classi: PNMutualExclusion , PNMutualExclusionSpec, SystemAnalysys, Boundedness, ModelProperties, MutualExclusion.



# Task 2: DESIGNER
Code and do some analysis on a variation of the Readers & Writers Petri Net: it should be the minimal variation you can think of, such that
if a process says it wants to read, it eventually (surely) does so. How would you show evidence that your design is right? What about a
variation where at most two process can write?

## Implementazione: 

//Vedi classi: Liveness, PNReadersWritersMinimal , PNReadersWritersMinimalSpec