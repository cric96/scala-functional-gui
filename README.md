# Scala Functional GUI 

![Build](https://github.com/cric96/scala-functional-gui/actions/workflows/scala.yml/badge.svg) [![Launch Scala Steward](https://github.com/cric96/scala-functional-gui/actions/workflows/scala-steward.yml/badge.svg)](https://github.com/cric96/scala-functional-gui/actions/workflows/scala-steward.yml)

This repository contains an example of a functional GUI leveraging Monix `Observable` (for reactive GUI) and `Task` (as the abstraction of IO Monad).

# Application developed
The focus of this example isn't on the application complexity, but on how it is possible to structure a full-fledge functional application. So we had decided to implement a simple game, the usual tic-tac-toe.

The insights here are:
- an immutable model that describes both data and operations.
- A view that uses monads to wrap side effect (in particular IO monad). We describe inputs as an ordered data stream that describes what happens view-side. It is well encoded with a (functional) Observable.
- A controller in which we coordinate view effect to model updates.

# Function Libraries used
## [Cats](https://typelevel.org/cats/)
> Cats is a library which provides abstractions for functional programming in the Scala programming language. The name is a playful shortening of the word category.
> Scala supports both object-oriented and functional programming, and this is reflected in the hybrid approach of the standard library. Cats strives to provide functional programming abstractions that are core, binary compatible, modular, approachable and efficient. A broader goal of Cats is to provide a foundation for an ecosystem of pure, typeful libraries to support functional programming in Scala applications.

Cats is the reference of functional programming in Scala. It is based on Category theory (a good free book can be found [here](https://github.com/hmemcpy/milewski-ctfp-pdf)). Many other libraries and framework are based on their abstraction. If you want to go futher with this library, you can read this free [book](https://underscore.io/books/scala-with-cats/)
## [Monix](https://monix.io/)
> Monix is a high-performance Scala / Scala.js library for composing asynchronous, event-based programs.

[cats-effect] has the same goal, but for completeness I prefer Monix.

[//]: # (TODO add more details)

## Abstractions used
### [Task](https://monix.io/docs/current/eval/task.html)
> Task is a data type for controlling possibly lazy & asynchronous computations, useful for controlling side-effects, avoiding nondeterminism and callback-hell.

### [Observable]()
> The Observable is a data type for modeling and processing asynchronous and reactive streaming of events with non-blocking back-pressure
> 
`Observable` supports the [Functional Reactive Programming](http://wiki.haskell.org/Functional_Reactive_Programming). It seems to be a good way to manage GUI events (as done in [Elm](https://elm-lang.org/)).
A good and purely functional alternative of Observable (only pull-based version) is [Iterant](https://monix.io/api/current/monix/tail/Iterant.html).

# Interesting related projects
- [Indigo](https://github.com/PurpleKingdomGames/indigo) : a functional game engine written in Scala. It levarages Scala.js to create in-browser games.
