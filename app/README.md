# BlackJack

Blackjack is a casino banking game. It is the most widely played casino banking game in the world. It uses decks of 52 cards and descends from a global family of casino banking games known as Twenty-One.

## Tech Stack

### Backend - SCALA

+ http4s = "0.23.18"
+ catEffect = "3.4.9"
+ circe = "0.14.1"
+ doobie = "1.0.0-RC1"
+ newType = "0.4.4"
+ logBack = "1.4.6"
+ scalaTest = "3.2.15"

### DataBase

+ Postgres

### Frontand - React & Redux & TypeScript

+ redux/toolkit
+ rtk query
+ mui
+ sass
+ react router dom v6
+ classnames


## How to start?

### Prerequisites

Please install recent versions of the following before the first lecture:
- [Scala plug-in](https://www.jetbrains.com/help/idea/discover-intellij-idea-for-scala.html) for IntelliJ IDEA
- Java Development Kit (JD), such as OpenJDK, e.g. [AdoptOpenJDK](https://adoptopenjdk.net/)
- [Scala](https://www.scala-lang.org/download/)
- [SBT](https://www.scala-sbt.org/download.html)
- [Node](https://nodejs.org/en/download)
- [Docker](https://www.docker.com/products/docker-desktop/)

### Backend

#### Docker 

From the source path, go to the dockers folder and launch the virtual container

```cmd
cd ./api/docker
docker compose up
```

### Scala

From the source path, go to the api folder and run it

```cmd
cd ./api
```

```scala
sbt
project eventStore
run
```


### Frontend

From the source path, go to the app folder and run it

```node
  yarn install
  or npm install
  yarn start 
  or npm start
```