hier kommt rein, was die platform bereitstellen soll:
-
-
-


Managed Commands auf Serverseite
---------------
Im Server sollten Commands managed sein. Hier ist noch nicht 100% geklärt, worauf wir da am besten aufsetzten. Im Idealfall gibt es wahrscheinlich 3 Implementierungen:#
- JavaEE CDI
- Spring
- Google Guava

Für JavaEE gibt es verschiedene Links die beschreiben wie man durch Nutzung der CDI Spec Managed Beans erstellen kann:
https://rmannibucau.wordpress.com/2013/08/19/adding-legacy-beans-to-cdi-context-a-cdi-extension-sample/
https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html
Für Spring haben wir das ganze bereits im Ansatz für die KaPo Demo realisiert


PresentationModel <-> Object Mapping
---------------
Für die Dolphin Platform wär es cool, wenn man direkt mit Objekten arbeiten könnte. Hierbei würden die PresentationModels in Objekte gemappt. Michael experiementiert aktuell in diesem Bereich. Will man dies hinbekommen, muss man sicherlich interne Attribute definieren die vond er Dolphin-Platform automatisch in PMs gesetzt und verwaltet werden.
In JavaFX hätte man dann sicher eine Art Binding-Klassen. Vielleicht kann man das ganze ähnlich (wie von Michael vorgeschlagen) wie JPA aufbauen. Man kann dann ein Java Pojo definieren und durch Annotations das Binding nach Dolphin festlegen. 
In javaScript wär es sicher cool, wenn man einfach JavaScript Objekte hätte auf die man wie gewohnt drauf zugreifen kann:
	myObject.name


UI-Binding auf ClientSeite
---------------
Schaut man sich aktuelle JavaScript Frameworks wie Angular oder Polymer an erkennt man, dass hier Bindings immer direkt im HTML gesetzt werden:

	<button title={myBinding}>

In JavaFX geht man aktuell einen anderen Weg und definiert die Bindings im Controller indem man die Gui-Komponenten in den Controller injected.
Er wär sicherlich Sinnvoll, wenn man für die Dolphin Platform hier einen einheitlichen Weg hätte, wobei aktuell beide Varianten vor und Nachteile haben:
In der JavaScript Variante hat man (theoretisch) UI-Toolkit unabhängige Controller da das Property- & Action-Binding in der View passiert
In JavaFX hat man zwar UI-Toolkit spezifische Controller, dafür besitzt die UI-Definitionen nun Kenntniss der Logik. 
Da wir mit Nutzung der Dolphin-Platform aber so wenig Controller wie möglich auf der CLient-Seite haben wollen, finde ich aktuell die erste Variante sehr cool. Dazu kommt, dass die ersten Projekte alle auf JS basieren und wir somit weniger Aufwand zum Start hätten. Für JavaFX würd edas bedeuten, dass man im Idealfall FXML so aufbort, dass man hier direkt Bindings zu Dolphin angeben und z.B. bei ButtonClicks Dolphin-Commands definieren kann.
Allerdings sollte man bei der Überlegung iOS und Android nicht außen vor lassen. Ich denke, dass diese beiden Platformen wichtig sind, wenn wir mit der DOlphin Platform erfolg haben wollen. Möglicherweise muss man hier dann mit Zwischen-Klassen (Binder-Klassen) arbeiten


Lifecycle von Views
---------------
Die meiste Zeit wird man mit der Dolphin-Platform sicherlich Dialoge erstellen. Hier ist es meiner Meinung nach sinnvoll einen default Lifecycle zu definieren.
Ich könnte mir zum Beispiel folgendes vorstellen:
Jeder Dialog-Typ ist durch ein PM-Typ definiert. Sobald auf Serverseite ein neues PM vom typ erstellt wird geht der Client her und erzeugt den passenden Dialog.
Auf Client-Seite gibt es eine Art Routing in dem definiert ist, welche PM-Typen zu welchem Dialog gehören und wie dieser dann angezeigt wird. Das ist im Web relativ einfach, da man hier in der Regal immer einfach einen Dialog anzeigt. Sobald man aber z.B. einen Desktop-Client hat in dem Tabs erlaubt sind sieht das ganze schon anders aus. Aber auch im Web will man sicherlich Fehlerdialoge, globale Notifications, etc. anzeigen.

Context vom PresentationModel
---------------
Schnell kommt man an den Punkt, dass man entweder Listen von Daten anzeigen will oder den gleichen Datentyp 2x auf dem Bildschirm hat. In diesem Fall muss dem PM ein Kontext zugeordnet werden. Dieser Context muss sowohl auf Client- als auch auf Serverseite bekannt sein.

Idee zum Aufbau von Commands auf Severseite
---------------
Bisher sind wir (Baloise & KaPo) hergegangen und haben pro Command eine Klasse definiert. Das ist nicht ganz so sinnvoll, da man innerhalb eines Dialogs oft mit den gleichen Services etc. arbeitet. Hier könnte man dann eine Basis-Klasse für den spezifische Dialog machen und davon alle konkreten Command-Klassen des Dialogs ableiten. Besser fände ich aber die Möglichkeit, dass ich pro Dialog einfach eine Klasse habe. In dieser Klasse kann ich nun mehrere Methoden definieren die für Commands stehen. Diese können dann durch Annotations definiert und einem speziellen Command zugeordnet werden. Beispiel:
	@DolphinManaged
	public class MyDialogHandler {
		
		@DolphinCommand(„MyDialogInit“)
		public void init() {…}

		@DolphinCommand(„MyDialogSave“)
		public void save() {…}

		@DolphinCommand(„MyDialogClose“)
		public void close() {…}
	}
Hier hab ich noch ein paar weitere Ideen zum Aufbau, will aber erstmal abwarten, was sonst so an Ideen reinkommt :)

Aufbau der Module (Ideen)
---------------

Hier muss man erst einmal zwischen den Programmiersprachen unterschieden. Das meiste macht allerdings der Java-Teil aus.

Java:
- Core-Modul: Das ganze wird sowohl im Client als auch im Server genutzt und beinhaltet allgemeine Klassen
- Server-Modul: Grundlegende Klasse, Interfaces und Annotations für die Server-Seite. Hier ist definiert, wie man mit Commands und PMs im Server arbeitet. Das ganze ist allerdings noch JavaEE / Spring unspezifisch. Die Intergration in einem spezifischen Web-Container liegt in extra Modulen
- Server-JavaEE-Modul: CDI-Extension um Dolphin Commands in einem JavaEE 7 Server managed nutzen zu können
- Server-Spring-Modul: Spring-Extension um Dolphin Commands in einem Server Server managed nutzen zu können
- Client-Modul: Allgemeine Klassen für die Integration der Dolphin Platform auf Client-Seite. Das ganze sollte (mit zukünftiger Betrachtung von iOS & Android) keine JavaFX spezifischen Klassen beinhalten. Dazu kommt, dass man hier wegen Android keine Java8 Features nutzen darf (RetroLambda?).
- JavaFX-Client-Modul: Integration der Dolphin-Platform für einen JavaFX Client. Hier ist z.B. definiert, wie die Bindings in javaFX realisiert werden etc.

Zukunft:
- iOS-Client-Modul / Android-Client-Modul: Im iOS Umfeld sollte mit RoboVM gearbeitet werden, da somit komplett in Java Entwicklet werden kann. In RoboVM sind für alle Nativen iOS Kompoenten Wrapper-Klassen vorhanden, so dass man mit RoboVM komplett native Anwendungen in Java bauen kann.

JavaScript:
- JavaScript-Client-Modul: Beinhaltet eine JS basierte Iplementierung des Clients. Hier muss dann auch der Inhalt des Java-Client-Moduls nachimplementiert werden

- AngularJS-Modul: Brauche wir das? Ich finde den ICOS Ansatz (Dolphin & WebComponents) für die Zukunft sehr interessant. Bei der KaPo werden wir zwar auf Angualr setzten, im großen und ganze könnte der ICOS Weg aber die richtige Richtung sein.


Update 1 - Grundlegender Aufbau des Servers
---------------
Die Dolphin Platform Server Komponente basiert auf der Servlet API 3.1 und instanziiert sich innerhalb eines Web-Containers automatisch. Die DolphinPlatformBootstrap Klasse implementiert das in der Servlet API definierte Interface ServletContainerInitializer. Implementierungen dieses Interfaces werden beim Application-Start automatisch geladen (Durch ServiceLoader Definition in META-INF/services). Hierdurch startet der Dolphin Plattform Boostrap automatisch. Durch den Start werden die beiden benötigten Servlets (Dolphin-Servlet und Dolphin-Invalidation-Servlet) angelegt und registriert. Das DolphinServlet bekommt automatisch eine Liste der Klassen übergeben die von Dolphin gemanaged werden sollen (Definiert durch die DolphinManaged Annotation). Dies werden die DolphinCommands sein. Sobald eine neue Session erstellt wird, ruft das DolphinServlet den DolphinCommandManager auf. Dieser ist Framework-Abhängig (z.B. Spring oder JavaEE) und kümmert sich darum, dass alle Dolphin Commands automatisch erstellt werden und im richtigen Scope liegen. Der DolphinCommandManager ist im Server-Modul nur als Interface definiert und es muss immer genau eine passende Implementierung geben. Diese wird später in den Framework-Spezifischen Modulen (Spring, etc.) hinzugefügt. Intern wird der ServiceLoader zum Laden und Instanziieren genutzt. Daher müssen alle Implementierungen des DolphinCommandManager Interfaces einen Default-Konstruktor haben.
Durch dieses Vorgehen kann man die Dolphin-Platform Jars einfach zu einer Anwendung hinzufügen und muss nichts mehr konfigurieren. In der eigentlich Anwendung müssen dann nur noch die Command-Klassen definiert und anmontiert werden. Auch eine web.xml wird hier nicht mehr benötigt. Später kann man sich überlegen, ob man ähnlich der persistence.xml eine Datei zur Konfiguration der Dolphin-Platform unterstützen möchte. Hierdurch könnte man in einer Anwendung dann z.B. das Default-Mapping der Servlets überschreiben.

Update 2 - CDI Implementierung für JavaEE
---------------
Habe eine Basis-CDI-Implementierung für JavaEE hinzugefügt. Diese ist allerdings momentan noch ungetestet. Die Implementierung nutzt Apache DeltaSpike um programmatisch Referenzen zu CDI Beans zu erstellen. Hierbei werden Beans für alle Klassen die mit der @DolphinManaged Annotation versehen sind erstellt. Alle diese Beans liegen automatisch im SessionScope.
Theoretisch sollte eine Klasse auf Serverseite nun wie folgt aussehen:

	@DolphinManaged
	public class MyViewController {

		@Inject
		private ServerDolphin dolphin;

		@Inject
		private MyService myInjectedService;

		@DolphinCommand(„unique-command-id-for-save“)
		public void save() {…}

		@DolphinCommand(„unique-command-id-for-load“)
		public void load() {…}
	}

Infos zu Scopes
---------------
Da der Server-Teil CDI nutzt muss sich hier auch Gedanken über Scopes gemacht werden. Beim ServerDolphin sollte das kein Problem sein. Der liegt per Definition schon im SessionScope da es auch ohne CDI genau einen ServerDolphin pro Session gibt. Bei den Commands sieht das aber schon anders aus. Der Scope der Command-Klassen ist theoretisch nicht definiert. Allerdings würde ich nicht hergehen und den vom Entwickler frei definieren lassen. Ich denke hier sollten wir einen Scope vorgeben. Am Einfachsten wäre wenn man entweder den SessionScope passend zum Dolphin verwenden würde. In dem Fall würde es pro Benutzer-Sessionen eine Instanz einer Command-Klasse geben. Eine andere Möglichkeit wär auch der Singleton-Scope. Hiermit würde es dann genau eine Instanz über alle Sessions geben. Die COmmand-Klassen sollten ja in der Regel stateless sein, da der Dolphin und alle Services injected werden. Daher sollte ein Singleton Scope möglich sein. Was meint ihr?


Ein paar Ideen zum Dolphin-OR-Mapping
---------------
Im Idealfall arbeitet man in der Dolphin-Platform nicht mehr mit PresentationModel und Attribut sondern direkt mit Java Objekten. Hierfür muss es dann allerdings einen OR-Mapper (oder eher OPM-Mapper) für Dolphin geben. Das ganze sollte im Idealfall an JPA angelehnt sein, da Entwickler sich hiermit bereits auskennen. Die Objekt-Repräsentation eines PresentationModel-Typs könnte dann zum Beispiel wie folgt aussehen:

	@Model(„My-PM-Type“)
	public class MyDataModel {
		
		@Attribute(„my-property-name“)
		private String name;

		@Attribute(value=„my-property-description“, tag=„my-tag“)
		private String description;

		@Child
		private MyCustomMetadataModel metadata;

		@Children
		private List<MyCustomEntryModel> entries;

		//getter & setter
	}

Wie bereits vorher erwähnt würde ich zu jedem Presentation-Model ein Context-ID definieren. Dieses Attribut ist also immer da. Daher gibt es in der Java-Klasse auch kein ID-Feld. Eine PM besitzt immer einen definierten Typ und eine Context-ID. Die PM-ID wird hierbei immer automatisch vom der Platform auf Serverseite erstellt. Will man die Id trotzdem haben kann man Problemlos folgendes Field in die Objekt-Klasse hinzufügen:

	@ModelId
	private long id;

Da Objekte durch den Model-Typ und die Context-ID definiert werden kann es somit 0,1 oder n passende Instanzen geben. Das ist meiner Meinung nach aber ok, da man bei der Entwicklung ja weiß, ob man nun eine Liste oder ein Objekt erwartet. Zum finden der Objekte wird es dann so etwas wie einen EntityManager geben der die benötigten Funktionen liefert:

	MyDataModel model = manager.find(MyDataModel.class, contextId);

Durch das Übergeben der Klasse kann der Manager den PM-Typ ermitteln (Reflection) und kann nun alle MPs von dem Typ finden. Über eine weitere Filterung werden nun die MPs rausgefiltert welche nicht die Context-ID haben. Diese ist immer als Attribut im PM vorhanden. Sollte es am Ende mehr als ein Objekt geben schmeißt der Manager eine Runtime-Exception. Will man eine Liste haben kann man analog folgendes tun:
	List<MyDataModel> models = manager.findAll(MyDataModel.class, contextId);
Wie man genau an die Context-ID kommt wird später geklärt und soll hier erst einmal nicht interessieren. Wichtig ist nur, dass sie die aktuelle Gui-Komponente in der das Objekt „lebt“ widerspiegelt. Ist man aber innerhalb des Models an der ContextID oder dem Typ interessiert kann man folgende Felder hinzufügen:

	@ModelType
	private String type;

	@ContextId
	private long contextId;

Wenn man nun die Child-Beziehung im Beispiel einmal außen vor lässt (dazu komm ich gleich) würde das passende PM wie folgt aussehen:

	PM(id=unique-long, type=„My-PM-Type“)
		Attribute(property-name=„my-property-name“)
		Attribute(property-name=„my-property-description“, tag=„my-tag“)
		Attribute(property-name=„context-id“)

Bei den Kindbeziehungen würde ich hergehen und immer mit zusätzlichen MPs arbeiten die eine Verknüpfung definieren. Alle diese MPs haben den gleichen Typ (z.B. „object-relation“) und definieren eine Vater-Kind-Beziehung. Ein solches PM könnte nun also wie folgt aussehen:

	PM(id=unique-long, type=„object-relation“)
		Attribute(property-name=„parent-id“)
		Attribute(property-name=„child-id“)

Wird nun ein Objekt geladen können einfach alle MPs gesucht werden bei dem das PM des zu ladenden Objektes als Parent eingetragen ist (durch Referenz der PM-id im „parent-id“ Attribut). Wir die @Child Annotation verwendet darf es nur 0 oder 1 passenden Eintrag geben. Ansonsten wird eine Runtime-Exception geworfen. Wird @Children verwendet darf es 0-n Einträge geben. Durch dieses Verfahren werden nun rekursiv die Objekte erstellt.
Will man Objekte erstellen oder löschen kann hier auch einfach der manager genutzt werden:

	//Erstellen eines neuen Models
	MyDataModel model = new 	MyDataModel();
	model.setName(…);
	manager.manage(model);

 	//Löschen eines Models
	manager.remove(model);

So kann man sicherlich relativ viel abbilden, allerdings bietet Dolphin einige zusätzliche Funktionen die hier noch nicht abgedeckt sind. Ein Problem ist sicherlich die Automatische Synchronisation. Gehen wir mal von folgender Code-Zeile aus:
	
	model.setName(„new Name“);

Hierdurch wird einfach das String field neu gesetzt, was keine Auswirkungen auf das PM hat. Um dies zu Lösen gibt es verschiedene Möglichkeiten:

Die erste Variante ist eine save() Methode im Manager. Hier muss man nun immer wenn man was im Objekt geändert hat die save Methode aufrufen:
	
	model.setName(„new Name“);
	manager.save(model);

Eine weitere Möglichkeit wären CDI Interceptors die aufgerufen werden sobald eine Command-Methode ausgeführt wurde. Diese gehen nun her und überprüfen die Modelle auf Änderungen. Alle Änderungen werden dann automatisch in die PMs übernommen. So muss der Entwickler sich nicht selber um die synchronisation kümmern.

Weitere Open Dolphin Funktion die durch dieses Model nicht unterstützt werden:
- Property-Change-Support
- Dirty Values
- Nutzung von Qualifier

Diese Funktionen kann man mit der beschriebenen Methode nicht so einfach Umsätzen. Will man dies Nutzen würde ich die Nutzung einer Trapper-Klasse vorschlagen. Diese Wrapper-Klasse bietet alle die definierten Funktion und kann in Model-Klassen für Attribute fields genutzt werden. Möchte man das ganze z.B. für das „description“ Field im Beispiel nutzen so würde dessen Definition nun wie folgt aussehen:

	@Attribute(value=„my-property-description“, tag=„my-tag“)
	private ModelAttribute<String> description;

In diesem Fall wird das Feld beim Erzeugen nicht einfach mit einem String gefüllt sondern mit einer ModelAttribute Instanz die als Wrapper dient. Diese Klasse stellt nun alle benötigten Funktionen zur Verfügung:

	MyDataModel model = manager.find(MyDataModel.class, contextId);
	model.getDescription().setValue(„new description“);
	String desc = model.getDescription().getValue();
	model.getDescription().addPropertyChangeListener(…)
	model.getDescription().reset();

Hier könnte man nun auch überlegen, ob man anstelle einer neuen Klasse direkt mit der Attribute Klasse aus Open Dolphin arbeitet. Möglicherweise beinhaltet diese aber Methoden die man hier nicht zur Verfügung stellen will. In dem Fall müsste man schauen, ob man das Interface in Dolphin aufteilt.
Zum Arbeiten mit Listen muss es noch eine Erweiterung geben. Oft will man sicherlich die Reihenfolge der Elemente in einer Liste definieren. Hierfür benötigen die Instanzen dann einen Index. Auch hier würde ich hergehen und eine Annotation definieren:

	@Index
	private long index;

Grundsätzlich würde ich vorschlagen, dass der Index auch immer automatisch als Attribut angelegt und gemanaged wird (wie die Context-ID). Bei einfachen Objekten steht dann halt immer eine 0 drin.

Ich bin einfach mal hergegangen und hab die hier beschriebenen Annotations im Projekt erstellt.
Was haltet ihr von dem Ansatz?


Beispiel für einen serverseitigen Controller
---------------
Basierend auf den oberen Beschreibungen könnte ein Controller für einen Dialog auf Serverseite nun wie folgt aussehen:

	@DolphinManaged
	public class MyViewController {

		@Inject
		private DolphinModelManager manager;

		@Inject
		private MyService myInjectedService;

		@DolphinCommand(„my-view-init“)
		public void init() {
			//Wird zum Start des Dialogs aufgerufen. 
			//Hier wird das PM des Dialogs erstellt was 
			//im Client dazu führt dass die View erstellt 
			//und an das hier erzeugte Model gebunden wird
			MyModel model = new MyModel();
			model.setName(„example“);
			manager.manage(model);
		}

		@DolphinCommand(„my-view-close“)
		public void close(long contextId) {
			//Wird aufgerufen und löscht das PM. 
			//Dies führt im Client dazu dass die View entfernt wird
			MyModel model = manager.find(MyModel.class, contextId)
			manager.remove(model);
		}

		@DolphinCommand(„my-view-save“)
		public void save(long contextId) {
			//Wird aufgerufen wenn in der View der 
			//Save-Button geklickt wird
			MyModel model = manager.find(MyModel.class, contextId);
			MyJpaEntity entity = new MyJpaEntity();
			entity.setName(model.getName);
			myInjectedService.saveEntity(entity);
		}
	}

Im Beispiel gibt es eine Besonderheit die noch nicht definiert wurde: die save(…) und close(…) Methoden erwarten als Übergabe-Parameter die context-ID. Diese dient ja dazu um einen spezifischen Dialog zu definieren und müsste übergeben werden. Dies wäre dann auch eine Besonderheit der Dolphin-Platform. Die ID ist aber auch am Client vorhanden und kann so automatisch von Client-Modul der Dolphin-Platform an den Server übermittelt werden.

Navigation zwischen Views
---------------
Basierend auf den bisherigen Ansätzen ist eine Navigation zwischen Views nicht schwierig zu implementieren. Auf Client-Seite wird die View immer automatisch erzeugt sobald das PM vorhanden ist. Im gezeigten Beispiel wird das PM erzeugt, sobald die Init-Methode aufgerufen wird. Da alle Command-Klassen managed sind, ist es auch kein Problem diese in andere Klassen zu injecten. Daher können wir relativ einfach eine Routing-Klasse definieren:

	@DolphinManaged
	public class MyAppRouting {
		
		@Inject
		private MyViewController controller1;

		@Inject
		private MyView2Controller controller2;

		@Inject
		private ErrorMessageController errorController;

		public void goToView2(MyDataModel modelToShow) {
			controller2.init(modelToShow);
		}

		public void showNewView1() {
			controller1.init();
		}

		public void showError(String message) {
			errorMessageController.init(message);
		}
	}

Diese Routing Klasse kann nun einfach in den DolphinController Klassen injected und genutzt werden:

	@DolphinManaged
	public class MyViewController {

		@Inject
		private MyAppRouting routing;

		@Inject
		private DolphinModelManager manager;

		@DolphinCommand(„my-view-save“)
		public void save(long contextId) {
			MyModel model = manager.find(MyModel.class, contextId);
			if(model.getName() == null) {
				routing.showError(„Kein Name angegeben!“);
			}
		}
	}

In diesem Beispiel wird nach drücken des Save-Button im Server eine Abfrage auf das „name“-Attribut gemacht. Ist dies null wird über das Routing ein Error-Dialog dargestellt. Hierbei wird durch den Aufruf der init(…) Methode der ErrorMessageController Klasse ein neues Error-Message-PM erzeugt was dazu führt, dass der Client drauf reagiert und eine Error-View (z.B. einen modalen Dialog) anzeigt.


Definition und Nutzung des Context
---------------
In den bisherigen Beschreibungen bin ich immer wieder auf den Context bzw. die Context-ID eingegangen ohne genau zu beschreiben was das überhaupt ist.
In der Dolphin Platform wird ja ein Model (basierend auf Open Dolphin) zwischen Server und Client synchronisiert. Dies bedeutet, dass sowohl die View im Client als auch der Controller  im Server das gleiche Model benutzten:

![Dolphin Model Sync](readme-data/basic.png "Dolphin Model Sync")

Stellen wir uns nun einmal vor, dass wir eine Desktop Anwendung mit einem zentralen TabPane haben. In diesem TabPane können (ähnlich wie beim Browser, Eclipse, etc.) beliebig neue Tabs hinzugefügt werden. Diese Tabs beinhalten dann einen Dialog / eine View der Anwendung:

![Tabs](readme-data/tabs.png "Tabs")

Jeder der Tabs ist somit aus Dolphin Sicht eine View und wird auch ein PresentationModel besitzen. Das PM wird ja im Server erzeugt und darauf hin fügt der Client den tab hinzu und erstellt das Binding zum PM. Stellen wir uns nun einmal vor, dass ein Ähnlicher (oder sogar der gleiche) Dialog 2x im geöffnet wird. Somit gibt es 2 Tabs in dem zum Beispiel jeweils eine Liste des gleichen Datentyps angezeigt wird.

![2 gleiche Ansichten](readme-data/2-views.png "2 gleiche Ansichten")

In diesem Fall werden nun pro Dialog im Server Daten / Presentation Models für die Listenansichten der beiden Dialoge erstellt. Jedes dieser Models muss nun einem Dialog bzw. einer der Listen in den Dialogen zugeordnet werden.

![Zuordnung der Daten](readme-data/list-data.png "Zuordnung der Daten")

Um definieren zu können wie die Modelle den Views zugeordnet werden schauen wir uns einmal an wie ein Presentation Model definiert ist:

![Definition eines Presentation Model](readme-data/pm-def.png "Definition eines Presentation Model")

Jedes Presentation Model in Open Dolphin besitzt eine eindeutige ID. Diese ID kann programmatisch vergeben werden oder wird automatisch von Open Dolphin gesetzt. Wichtig ist hierbei, dass die ID innerhalb einer Dolphin-Session eindeutig ist. Zusätzlich gibt es einen Typ. Dieser Typ kann programmatisch gesetzt werden und Open Dolphin bietet viele verschiedene Funktionen um z.B. nach Models eines Typs zu suchen oder auf die Erstellung von neuen Presentation Models eines bestimmten Typs zu reagieren. Die ID hilft uns bei unserem Problem nicht wirklich weiter, da wir im Client ja alle Daten von einem Bestimmten Typ (der Typ für die Daten der Listendarstellung) suchen. Hier ist also der PM Typ das wichtige Kriterium. Stellen wir uns also vor, dass wir verschiedene PMs mit dem passenden Typ besitzen. Alle PMs die diesen typ bestimmten können leicht gefunden werden.

![Suche eines Presentation Model Typs](readme-data/all-pms.png "Suche eines Presentation Model Typs")

Wie in der Grafik angezeigt, gibt es in einer Dolphin Session natürlich noch deutlich mehr PMs. Allerdings können einfach alle mit einem spezifischem Typ (in diesem Fall „my-list“) gefunden werden. Jetzt kommt es aber zu einem Problem: Es ist nicht definiert ob die einzelnen Modelle nun zu View A oder zu View B gehören. Würde man das vernachlässigen würden einfach alle Datensätze in beiden Dialogs angezeigt. Das ist ja definitiv nicht das was wir wollen.

![Kein Context vorhanden](readme-data/no-context.png "Kein Context vorhanden")

Im Grunde bedeutet dies, dass die PMs nicht den Context ihrer View kennen. Wären die Modelle einer spezifischen View / dem Context einer View zugeordnet, so könnte man einfach erkennen zu welcher Liste welches PM gehört. Aus diesem Grund ist die Idee der Context-ID entstanden. Hierbei hat jede View einen eigenen Context. Dieser Context spiegelt sich im Model wieder, so dass man jederzeit einfach erkennen kann zu welcher View ein Model gehört. Hierbei ist die Context-ID der View als Attribut im Model definiert:

![Beispiel des Context](readme-data/with-context.png "Beispiel des Context")

Durch dieses Vorgehen kann man jederzeit PMs einer spezifischen View zuordnen. Als Beispiel würde hier eine klassische Tab-basierte Anwendung gewählt. Das ganze ist aber auch in anderen Szenarien sehr sinnvoll. Bei der Baloise gab es z.B. keine Tabs. Allerdings gab es hier Dolphin basierte Widgets. Diese Widgets ware quasi eine kleine View die in andere Views beliebig integriert werden konnte. Ein Beispiel ist z.B. ein Benutzer-Suchfeld mit Autovervollständigung. Dieses Widget definiert einen eigenen PM Typ und besitzt auch einen eigenen Controller mit definierten Dolphin-Commands im Server. Fängt man an zu tippen wird durch die Commands automatisch eine Abfrage an die Benutzer-Stammdaten durchgeführt und die Ergebnisse als PMs an den Client geschickt um dann in einem Popup zur Autovervollständigung angezeigt zu werden. Nun kann man sich z.B. vorstellen, dass diese Komponenten 2x in einer View vorkommt:

![Beispiel für Widgets](readme-data/widget.png "Beispiel für Widgets")

Im gezeigten Beispiel ist das Widget eine ComboBox zur Länderauswahl und die MPs des Widgets definieren die Länder die ausgewählt werden können sowie das aktuell ausgewählte Land. Auch in diesem Fall müssen die Models wieder den Context des Widgets kennen zu dem sie gehören. Wäre dies nicht der Fall würden beide Widgets immer das gleiche anzeigen und sobald man in ComboBox A etwas auswählt würde das gleiche auch in ComboBox B angezeigt (was bei einer Übersetzungs-App wenig Sinn macht). In diesem Fall ist das Widget die View welche über eine Context-ID verfügt. In den PMs ist dann die jeweilige Context-ID definiert.
Es gibt natürlich auch andere Wege das ganze zu lösen. Man kann  z.B. auch hergehen und die Context-ID als Bestandteil des Types der MPs definieren. In den gezeigten Beispielen hatten die PMs immer alle den gleichen Typ. Hier könnte man nun aber mit einem Prefix oder Suffix arbeiten in dem die Context-ID definiert wird:

![Context-Bestimmung über den Type](readme-data/context-in-type.png "Context-Bestimmung über den Type")

Hierbei muss man dann Hilfsmethoden haben um die String-Konkatenationen durchzuführen und nach einer Typ-Contest-Kombination zu suchen. Das vorgehen hat sowohl einen Vorteil als auch einen Nachteil:
- Vorteil: Der Typ eines MPs kann nach der Erstellung nicht mehr geändert werden. Hierdurch ist es nicht möglich ein PM plötzlich einem falschen Context zuzuordnen indem man programmatisch den Inhalt des Context-Attributs ändert
- Nachteil: Hier wird mit String-Konkatenationen gearbeitet 