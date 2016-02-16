###### in the name of ALLAH

#### EasyConsole

##### Deps

```
libraryDependencies += "com.teanlab" %% "akka-easyconsole" % "0.1.0"
```

##### Imports

```scala
import com.teanlab.akkautil.easyconsole._
```

##### Code

```scala
val func: EasyConsole.Func = {
  case "hello" :: xs => "Hello :)"
  case "kill" :: "me" :: xs => "Never :)"
  case "kill" :: "you" :: xs => sys.exit(1)
  case "help" :: xs => "hello | kill me | kill you | help"
}
val prop = EasyConsole.props("localhost",1212,"Welcome; try 'help'"
actorSystem.actorOf(props,"easyconsole")
```

##### Use

```
$ telnet localhost 1212
Connected to localhost.
Escape character is '^]'.
Welcome; try 'help'
help
hello | kill me | kill you | help
kill me
Never :)
kill you
Connection closed by foreign host.
$
```