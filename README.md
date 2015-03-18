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
