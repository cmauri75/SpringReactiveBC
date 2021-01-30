Cloud, TDD, CI, Agile per essere più veloci, sbagliare prima e imparare dagli errori.
Cloud si trova bene nel comportametne elastico di crescita e discesa del demand
Piccoli team che lavorano su piccole parti in modo indipendente (importante) così sono veloci ed efficienti, non posso avere ae un jar che distribuisco perchè creo dipendenza, 
Però creo nuovi problemi che non c'erano nel mondo monolitico.

Normalmente ho diverse fonti dati da cui reperisco informazioni, diversi DB, servizi esterni, code, etc.. 
RP astrae queste sorgenti permettendo:
-  comporle in modo semplice.
- gestire gli errori che possono sorgere
- ti fa scrivere codice molto più multi-thread e resource-efficient

Creiamo il progetto su: https://start.spring.io/
Primo faciamo una reactive webapp con spring boot, lombok e actuator, config client, H2 database, Spring data r2dbc: chiamo customer
Usiamo le api reactive che, a differenza di quelle normali dove faccio una richiesta e attendo una risposta, mando assieme alla richiesta una callback e mi sgancio. Sarà poi l'executor ad invocare la reazione di risposta asincronamente.

Creo Customer e CustomerRepository

Clicco su ReactiveCrudRepository per mostrare i metodi:
NB: Publisher, è un Future che pubblicherà i dati una volta pronti, è qualcosa che pubblica dati asincronamente. Un consumer può registrarsi per ascoltare i risultati man mano che arrivano.
Ho 2 specializzazioni: Mono che pubblica 0 o 1, Flux che ne può pubblicare 0-->n, 

Torno nel main e creo 
@Bean
ApplicationListener CTRL+S
return new CTRL+S

Crea il DB aggiungengo il dbc e lanciando l'sql

poi lancia e vedi che non bomba, magari passa al metodo più conciso, e inserisci i log

Adesso creo il rest-controller e poi provalo.

ORa nelle properties aggiungo functionality all'activator, una serie di management bean che mi permettono di controllare il mio ambinete, possono essere utilizzate da kubernate per monitorare il funzionamento.
in particolare c'è health

Creo un controller per bloccare l'app. 
curl -X POST localhost:8080/stop

Posso dirgli di fare uno shutdown gracefull sempre nelle props

adesso creo un container usando le commodity fornite dal plugin springboot:
mvn spring-boot:build-image

Ai morsetti esterni prende del codice di diverso genere e ci crea un container analizzandone il contenuto. Puoi comunque farti il tuo Docker file, ma così è più veloce e consistente
docker run -p8080:8080 rwa:0.0.1-SNAPSHOT

Ora faccio un secondo microservizio, con lombok, Config client e:
 RSocket, un metodo di call sviluppato da netflix, binario molto efficente (più di http2) e funziona molto bene con le reactive
 orders

Creo dei dati di test e il mio controller rsocket

ho bisogno di un client:
wget -O rsc.jar https://github.com/making/rsc/releases/download/0.4.2/rsc-0.4.2.jar
java -jar rsc.jar tcp://localhost:8081 --stream -r orders.3

Adesso mi serve il gateway, terzo microservizio.

