
# Collektive Exercises

This repository contains a set of exercises developed using the [Collektive](https://collektive.github.io/) framework.  
Each exercise explores key concepts in aggregate programming, such as distributed distance computations, leader election, and local-to-global coordination.

---

## 🔁 How to Use

To run a specific aggregate function on all nodes, you need to modify the `entrypoint` function:

```kotlin
fun Aggregate<Int>.entrypoint(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Any? {
    val programOutput: Any = getLocalId() // ← Replace this with your custom aggregate function
    ...
}
```
Replace `getLocalId()` with any of the defined functions (e.g., `distanceToSource()`, `bullsEye(metric)`, `multiLeader()`, etc.) to visualize or test different behaviors.


### 🔁 Switching Network Topologies
The file [simulation-environment.yml](simulation-environment.yml) allows you to switch between different network topologies:

*Simple* network:
```yaml
deployments:
  - type: GraphStreamDeployment
    parameters: [15, 2, 0, PreferentialAttachment]
    programs:
      - *program
    contents:
      - molecule: isSource
        concentration: true
```
*Complex* network (Grid 20x20):

```yaml
deployments:
- type: Grid
  parameters: [0, 0, 20, 20, 1, 1, 0.3, 0.3]
  programs:
  - *program
```

To switch the network topology:

- Uncomment the block you want to use.
- Comment the other one.
- Then run the simulation again.

---

## 🚀 How to Run

To run the simulation, use the following command from the root directory:

### Linux / macOS:
```bash
./gradlew runInSimulation
```

### Windows (PowerShell):
```powershell
.\gradlew.bat runInSimulation
```

> This will start the simulation in the Alchemist UI environment.

---

## 📚 Exercises Overview

### `getLocalId()`
Returns the current node's `localId`.

### `incrementValue()`
Starts from 0 and increments by 1 at each computational round.

### `maxValueFromNeighbours()`
Returns the maximum `localId` among neighboring nodes. Returns `0` if isolated.

### `searchSourceNS()`
Finds the node with the smallest `localId` **without using standard library functions**.

### `searchSource()`
Finds the node with the smallest `localId` **using standard library utilities**.

### `distanceToSource()`
Calculates hop-based distances from the source (node with smallest `localId`).

### `distanceToSource(metric)`
Uses Bellman-Ford with a provided metric (`Field<Int, Double>`) to calculate distances.

### `networkDiameter()`
Computes the hop-count-based diameter of the network.

### `bullsEye(metric)`
Creates a "bullseye" pattern by:
- Identifying two distant nodes,
- Approximating the center using diagonal intersections,
- Mapping distance from center to gradient values (for visualization).

The result resembles concentric zones around the center.

Or visually: [bullseye.png](static/bullseye.png)

### `multiLeader()`
Elects multiple leaders using a bounded strategy. Nodes compute hop distances to their group leader.

### `distanceToSource(sourceIDs: List<Int>)`
Computes hop-distance to the nearest node in a list of source IDs.  
Returns a `SourceDistance(sourceID, distance)` object.

---

## 🔗 Documentation

### 📘 Collektive Documentation
- Official site: [https://collektive.github.io](https://collektive.github.io)

### 🧪 Alchemist Documentation
- Alchemist (the simulation platform powering the UI):  
  [https://alchemistsimulator.github.io](https://alchemistsimulator.github.io)

- Swing UI shortcuts (keyboard controls and interactions):  
  [Alchemist Swing Shortcuts Reference](https://alchemistsimulator.github.io/reference/swing/index.html)
