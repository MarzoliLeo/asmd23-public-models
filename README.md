# Task 1: VERIFIER
Code and do some analysis on the Readers & Writers Petri Net. Add a test to check that in no path long at most 100 states mutual
exclusion fails (no more than 1 writer, and no readers and writers together). Can you extract a small API for representing safety properties?
What other properties can be extracted? How the boundness assumption can help?

## Implementazione: 

Nell'implementazione, sono state utilizzate le seguenti classi e tecniche:
1. **[PNReadersWriters](src/main/scala/u06/examples/PNReadersWriters.scala)**: Questa classe rappresenta il Petri Net dei Readers & Writers, con transizioni modificate per garantire la mutual exclusion.
2. **[MutualExclusion](src/main/scala/u06/modelling/MutualExclusion.scala)**: Una classe che modella la proprietà di safety legata alla mutual exclusion, che assicura che non ci siano violazioni tra scrittori e lettori attivi contemporaneamente.
3. **[Boundedness](src/main/scala/u06/modelling/Boundedness.scala)**: Verifica che il numero di token in ogni posto del Petri Net non superi una soglia massima (in questo caso, 10 token).
4. **[SystemAnalysis](src/main/scala/u06/modelling/SystemAnalysis.scala)**: Fornisce funzioni per generare percorsi completi attraverso il Petri Net e filtrarli per garantire che rispettino certe proprietà.

Infine, il nuovo modello può essere testato tramite la classe: [PNReadersWritersSpec](src/test/scala/u06/modelling/Task1/PNReadersWritersSpec.scala).

Questa implementazione permette di verificare le proprietà di mutual exclusion e boundedness per il modello di Readers & Writers. La separazione delle proprietà di safety in classi specifiche come MutualExclusion e Boundedness consente una maggiore riusabilità e chiarezza nel codice. Ulteriori proprietà possono essere aggiunte facilmente seguendo questo approccio modulare.



# Task 2: DESIGNER
Code and do some analysis on a variation of the Readers & Writers Petri Net: it should be the minimal variation you can think of, such that
if a process says it wants to read, it eventually (surely) does so. How would you show evidence that your design is right? What about a
variation where at most two process can write?

## Implementazione: 

Si è definito un **Petri Net** minimale con transizioni che gestiscono le richieste di lettura e scrittura. I lettori devono attendere fino a quando non c'è un processo di scrittura in corso, mentre gli scrittori possono accedere al loro turno solo se non ci sono lettori attivi. Inoltre, si è limitato il numero di scrittori concorrenti a due.

- **[Liveness](src/main/scala/u06/modelling/Liveness.scala)**: Se un processo chiede di leggere, alla fine gli sarà garantita la lettura. Questo è stato implementato aggiungendo una transizione che garantisce che il lettore passi da uno stato di attesa (WaitToRead) allo stato di lettura (Reading).

- **[MutualExclusion](src/main/scala/u06/modelling/MutualExclusion.scala)**: Assicuriamo che lettori e scrittori non possano accedere simultaneamente alle risorse, creando transizioni che vietano la scrittura quando è in corso una lettura, e viceversa.

### Verifica delle Proprietà
Si è verificato il modello come visibile nella classe [PNReadersWritersSpecMinimal](src/test/scala/u06/modelling/Task2/PNReadersWritersMinimalSpec.scala) generando percorsi di esecuzione con una profondità massima di 100, e analizzato ogni percorso per identificare violazioni alle proprietà di safety:
- **Liveness**: Controlla che ogni processo in attesa di leggere eventualmente possa farlo.
- **Mutual Exclusion**: Controlla che non ci siano stati in cui la lettura e la scrittura avvengano simultaneamente.

Nessuna violazione è stata trovata, fornendo evidenza che il design soddisfa le proprietà richieste.

# Task 3: ARTIST
Create a variation/extension of PetriNet meta-model, with priorities: each transition is given a numerical priority, and no transition can
fire if one with higher priority can fire. Show an example that your pretty new “abstraction” works as expected. Another interesting extension
is “coloring”: tokens have a value attached, and this is read/updated by transitions.

## Implementazione:

Sono state introdotte due estensioni principali al meta-modello di PetriNet: **[PetriNetWithPriority](src/main/scala/u06/modelling/PetriNetWithPriority.scala)** e **[PetriNetWithColoring](src/main/scala/u06/modelling/PetriNetWithColoring.scala)** dei token.

### PetriNetWithPriority
In questa estensione, ogni transizione ha una priorità numerica. Quando più transizioni sono abilitate contemporaneamente, solo quella con priorità maggiore può essere eseguita. Questo permette di gestire scenari in cui determinati eventi devono avere precedenza su altri, garantendo che le regole più importanti vengano rispettate.

L'implementazione organizza le transizioni in ordine decrescente di priorità. Durante l'esecuzione, la rete analizza solo le transizioni più alte e ne esegue una se possibile. Un esempio classico potrebbe essere un processo con più task, dove quelli più critici devono essere eseguiti prima rispetto a quelli meno importanti.

### PetriNetWithColoring
In questo modello, i token presenti nei posti della rete hanno un "colore", che rappresenta un valore aggiunto (es. una caratteristica o un ruolo). I token colorati possono essere letti e aggiornati dalle transizioni. Questo approccio permette di gestire situazioni complesse in cui lo stato di un token non è determinato solo dalla sua presenza in un determinato posto, ma anche da ulteriori attributi.

Il cambiamento principale è stato effettuato su [ColoredMSet](src/main/scala/u06/utils/ColoredMSet.scala), che è stato esteso per rappresentare multiset di token colorati. Ora è possibile associare un valore (il "colore") a ciascun token, e le operazioni sulle reti Petri possono leggere o modificare questi valori.

Ad esempio, nella simulazione di un sistema Readers-Writers, ogni token potrebbe rappresentare un lettore o uno scrittore, e le transizioni leggerebbero e modificherebbero i valori associati a questi token per modellare correttamente il comportamento di lettura e scrittura.

### Esempi
- **[PetriNetWithPriorityExample](src/main/scala/u06/examples/PNWithPriorityExample.scala)**: Supponiamo di avere una rete con due transizioni, una con priorità più alta dell'altra. Quando entrambe sono abilitate, viene eseguita prima la transizione con priorità più alta, garantendo che le operazioni più urgenti abbiano sempre la precedenza.
- **[PetriNetWithColoringExample](src/main/scala/u06/examples/PNWithColoringExample.scala)**: Nel sistema Readers-Writers, i token "colorati" rappresentano i lettori e gli scrittori. Le transizioni aggiornano questi token in base alle regole del sistema, garantendo che più lettori possano leggere contemporaneamente ma che uno scrittore abbia accesso esclusivo.

Queste estensioni offrono maggiore flessibilità e precisione nel modellare sistemi complessi.


# Task 4: TOOLER
The current API might be re-organised: can we generate/navigate all paths thanks to caching and lazy evaluation? can we use
monads/effects to capture non-determinism? can we generate paths and capture safety properties by ScalaCheck?

## Implementazione:

Entrambe le feature richieste dal task sono state apportate estendendo la classe: [SystemAnalysis](src/main/scala/u06/modelling/SystemAnalysis.scala)

### Caching e Lazy Evaluation

Per ottimizzare l'esplorazione di percorsi, utilizziamo una combinazione di **lazy evaluation** e **caching**. Questo ci permette di generare i percorsi in modo "pigro" (ossia solo quando sono effettivamente necessari), evitando di ricalcolare percorsi già esplorati. In questo modo, otteniamo un significativo risparmio in termini di tempo e risorse computazionali, specialmente quando si gestiscono sistemi complessi come il problema dei Readers & Writers. La funzione `completePathsLazy` implementa questo approccio, dove i percorsi vengono memorizzati man mano che vengono esplorati, per essere poi riutilizzati.

### Cattura del Non-Determinismo

Il non determinismo è un aspetto critico nell'analisi di sistemi concorrenti, come nel caso del problema dei Readers & Writers. Viene catturato utilizzando strutture monadiche (come `Future`) che permettono di gestire la ramificazione dei percorsi esplorati. Questo ci consente di esplorare tutte le possibili evoluzioni del sistema e di verificarne la correttezza. La funzione `completePathsNonDeterministic` è un esempio di come catturiamo il non determinismo e esploriamo i percorsi in parallelo, mantenendo il controllo su tutte le possibili evoluzioni.

## Verifica delle Proprietà di Safety

Un aspetto fondamentale di questo task è la verifica delle **proprietà di safety** con la classe [PNReadersWritersNewTools](src/test/scala/u06/modelling/Task4/PNReadersWritersNewTools.scala), che nel nostro caso includono:
- **Mutual Exclusion** e **Boundness**.

### ScalaCheck per la Verifica

La classe [PNReadersWritersScalaCheck](src/test/scala/u06/modelling/Task4/PNReadersWritersScalaCheck.scala) è stata utilizzata per generare automaticamente percorsi e stati possibili, verificando le proprietà di safety su questi percorsi generati. Grazie ai generatori di ScalaCheck, possiamo esplorare in maniera efficace sia percorsi deterministici che non deterministici, valutando se violano o meno le proprietà di mutua esclusione e boundedness. Questo approccio automatizzato facilita l'identificazione di eventuali violazioni in un insieme di percorsi generati casualmente, permettendoci di scalare il testing in modo efficiente.

In sintesi, grazie a caching, lazy evaluation, monadi per il non determinismo e ScalaCheck per la generazione automatica di percorsi, riusciamo a catturare l'intero spazio degli stati e verificare le proprietà di safety del sistema.



# Task 5: 