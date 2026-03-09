# Akka Http Webgoat

A vulnerable Akka HTTP showcasing potentially vulnerable code patterns.

## Build Token

To build locally, you need to fetch a token at https://account.akka.io/token that you have to place into `~/.sbt/1.0/akka-commercial.sbt` file like this:
```
ThisBuild / resolvers += "lightbend-akka".at("your token resolver here")
```

## How To Run

```
# Enter sbt
sbt

# Restart app
reStart
```