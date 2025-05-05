# Combining Spatial Computing Blocks:

#### Basic exercises [-> `BasicExercises.kt`](src/main/kotlin/collektive/exercises/BasicExercises.kt):
1. Select a node identified as `source`, chosen by finding the node with minimum uid in the network. 
2. Compute the distances between any node and the `source`.
3. Calculate in the source an estimate of the true diameter of the network (the maximum distance of a device in the network).
4. Broadcast the diameter to every node in the network.

#### Advanced exercises [-> `AdvancedExercises.kt`](src/main/kotlin/collektive/exercises/AdvancedExercises.kt):
5. Consider the source identified in exercise 1, determine nodes 3 hops away from the source.
6. Consider a set of sources, determine the number of hops towards the nearest source.
7. Determine the max number of hops in the node neighborhood towards the nearest source identified in exercise 6.
