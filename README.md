# benchmark-socket
A benchmark application comparing performance aspects of various transport approaches:
* Socket
* Rsocket
* ZMQ

## Execution
Project consists of 2 separate applications. They are configured to be automatically compatible for each `transport`.
- Pitcher: Responsible for _throwing_ the messages using the specified `transport`
- Catcher: Responsible for _receiving_ using the specified `transport`

Running a benchmark is a matter of running a `Pitcher` and `Catcher` pair specifying the correct main class as well as a transport type.
```bash
# For Pitcher
java -jar benchmark-socket.jar dev.tomek.benchmarksocket.pitcher.Pitcher --transport=<transport-name>

# For Catcher
java -jar benchmark-socket.jar dev.tomek.benchmarksocket.catcher.Catcher --transport=<transport-name>
```

Possible transports:
```bash
socketPlain
socketRefined
rsocket
zmq
```

## Results
Here are sample results from my runs.

### Specs

All results presented here have been performed on the same machine for comparability

| CPU                               | CORES   | RAM        |
| --------------------------------- | ------  | ---------- |
| Inter Core i7-4720 @ 2.60GHz      | 4       | 8GB DDR3   |

JDK version
```
openjdk 13 2019-09-17
OpenJDK Runtime Environment (build 13+33)
OpenJDK 64-Bit Server VM (build 13+33, mixed mode, sharing)
```

### Results

| APPROACH                          | THROUGHPUT min    | THROUGHPUT max    |
| --------------------------------- | ----------------- | ----------------- |
| Socket plain                      | 32 922 /s         | 35 658 /s         |
| Socket refined                    | 4 106 492 /s      | 4 620 004 /s      |
| ZMQ                               | 1 407 096 / s     | 1 212 582 / s     |
| Rsocket                           | 117 324 / s       | 147 148 / s       |
