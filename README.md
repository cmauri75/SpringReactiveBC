RP permette di creare servizi più efficienti e resilienti che si sposano bene con gli ambienti cloud a microservizi.
E' tutto un mondo nuovo in cui abbiamo una serie di tecnologie coesistenti. Tecnologie ma anche filosofia.

Cloud, TDD, CI, Agile per essere più veloci, ho piccoli team agili (ossia veloci ed efficienti) che si occupano di un singolo task, veloci che possono permettersi di sbagliare prima,
imparare dagli errori e arrivare ad un risultato migliore. Piuttosto che progettare tutto prima.
Questo comporta ad esempio non posso avere ae un jar che distribuisco perché creo dipendenza, per contro avrò molto scambio di dati in rete.
Con servizi che possono non essere disponibili ma anche ridondare.
RP è un tassello

Però creo nuovi problemi che non c'erano nel mondo monolitico.

Normalmente ho diverse fonti dati da cui reperisco informazioni, diversi DB, servizi esterni, code, etc.. 
RP astrae queste sorgenti permettendo:
- comporle in modo semplice.
- gestire gli errori che possono sorgere
- ti fa scrivere codice molto più multi-thread e resource-efficient

Creiamo il progetto su: https://start.spring.io/
Primo facciamo una reactive webapp con spring boot, lombok e actuator, config client, H2 database, Spring data r2dbc: chiamo customer
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

Alibaba è fatto con questa filosofia.

