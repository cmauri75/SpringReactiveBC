RP è un framework che permette di creare servizi più efficienti, resilienti e dinamici che si sposano bene con gli ambienti cloud a micro-servizi.

E' tutto un ecosistema nuovo in cui abbiamo una serie di tecnologie coesistenti. Tecnologie ma anche filosofia, noi in particolare vedremo come creare un sistema reactive con Spring.

Parlando in astratto abbiamo:
Cloud, TDD, CI, Agile per essere più veloci, nel nuovo ecosistema abbiamo più piccoli team agili (ossia veloci ed efficienti) che si occupano di un singolo task, veloci affinchè possono permettersi di sbagliare prima,
imparare dagli errori e arrivare ad un risultato migliore, lavorando a spirale. Piuttosto che progettare tutto prima.

Questo comporta limiti operativi, ad esempio non posso avere a un jar che distribuisco perché creo dipendenza, avrò però in cambio molto scambio di dati in rete.

Questi servizi devono essere pensati in modo che possano non essere disponibili ma anche ridondare.
RP è un tassello nell'econistema.

Anche se creranno nuovi problemi che non c'erano nel mondo monolitico.

Normalmente ho diverse fonti dati da cui reperisco informazioni, diversi DB, servizi esterni, code, etc..  
Posso vedere il mio progetto come una serie di flussi di dati da elaborare e trasmettere.

RP astrae queste sorgenti permettendo:
- comporle in modo semplice.
- gestire gli errori che possono sorgere
- scrivere codice nativamente multi-thread e resource-efficient senza neanche che te ne accorgi

Creiamo il progetto su: https://start.spring.io/

Primo facciamo una reactive webapp, con spring reative web, lombok, boot actuator, config client, H2 database, Spring data r2dbc: chiamo customer

Usiamo le api reactive che, a differenza di quelle normali dove faccio una richiesta e attendo una risposta, mando assieme alla richiesta una callback e mi sgancio. Sarà poi l'executor ad invocare la reazione di risposta asincronamente.

Mi arriverà nel tempo un flusso di dati che gestirò a dovere. Cambia il paradigma.

Nome: Customer. Generate e importo come modulo

Creo class Customer (dimentica ID sulla chiave) e interface CustomerRepository

Ctrl+Click su ReactiveCrudRepository per mostrare i metodi:

Notate: Publisher, è un Future che pubblicherà i dati una volta pronti, è qualcosa che pubblica dati asincronamente. 

Un consumer può registrarsi per ascoltare i risultati man mano che arrivano.
Ho 2 specializzazioni: Mono che pubblica 0 o 1, Flux che ne può pubblicare 0-->n, 

Torno nella classe e dentro il main main e creo 
@Bean
ApplicationListener ...

Crea il DB aggiungengo il dbc e lanciando l'sql
poi lancia e vedi che bomba, allora metti l'id, mostra che debuggare può diventare complesso e rilancia.
SHIFT+F10

Adesso creo il rest-controller col solo GET e poi provalo col browser.

Ora nelle properties aggiungo functionality all'activator, una serie di management bean che mi permettono di controllare il mio ambinete, possono essere utilizzate da kubernate per monitorare il funzionamento.
in particolare c'è health. Viene usato da kubernates per controllare se il container è acceso.

Creo un controller per bloccare l'app. 

curl -X POST localhost:8080/stop

adesso creo un container usando le commodity fornite dal plugin springboot:
mvn spring-boot:build-image

Ai morsetti esterni prende del codice di diverso genere e ci crea un container analizzandone il contenuto. Puoi comunque farti il tuo Docker file, ma così è più veloce e consistente
docker run -p8080:8080 customer:0.0.1-SNAPSHOT
---
Ora faccio un secondo microservizio, con lombok, Config client e:
 RSocket, un metodo di call sviluppato da netflix, binario molto efficente (più di http2) e funziona molto bene con le reactive
 
NOME: orders

scarico e unzippo.
Creo il bean Order e poi il controller rsocket

Attenzione ad utilizzare oggetti concorrenti perchè sarà tutto multithread.

Nelle props cambia la porta con: spring.rsocket.server.port=8081

ho bisogno di un client, cerca su google "rsc.jar client spring"

wget -O rsc.jar https://github.com/making/rsc/releases/download/0.4.2/rsc-0.4.2.jar

java -jar rsc.jar tcp://localhost:8081 --stream -r orders.3

---
Adesso mi serve il gateway, terzo microservizio. E' il punto che intercetta le richieste che arrivato alla rete, è qui che devo mettere AUTH, o redirect, routing, load balancing,
compression, 
Oltre a lombok, rsocket, config client, aggiungo reactive web e gateway

Prima creo un API gateway con spring gateway. 
Quando ricevo una richiesta ad una porta effettuo un route, per configurarle mi servono 3 cose: un predicato su cosa matchare, una destinazione e in mezzo un filtro, in cui posso fare di tutto.
curl -v -H "Host: test.spring.io" http://localhost:9999/proxy
Se cambio l'host non va più, posso fare comportamenti dinamici implementati anche su DB o nelle properties

Adesso prendo i dati e li trasformo.

CRMCLIENT
Mi copio i due bean order e customer, costruisco i mock dei getter con return Flux.empty() e inietto webclient e rsocketrequester, poi li popolo per fare le invocazioni

CustomerOrder
Creo una view verso le mie sorgenti, implementa la composition get customerorders e la invoco da browser.
Questa chiamerà in parallelo due servizi, un http e un rsoket, aggrega i risultati. 
Posso aggiungere operatori per potenziare il funzionamento. In fondo a getCustomers posso mettere .retry,... 
Ho un sacco di operatori built in che rendono more reliable, safe e scalable la mia funzionalità.

Alibaba e netflix sono fatti con questa filosofia.

