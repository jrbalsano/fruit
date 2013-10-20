Fruit salad

1. Compile the simulator
javac fruit/sim/*.java

2. Compile the distribution generator
javac fruit/sim/UniformFruitGenerator.java

if it is your fruit generator
javac fruit/gx/FruitGenerator.java

3. Run the simulator
java fruit.sim.Fruit <playerlist> <bowlsize> <distribution generator> <gui> <trace>

e.g.
java fruit.sim.Fruit players.list 12 fruit.sim.UniformFruitGenerator true

4. Implement your Player and FruitGenerator

5. You can pull updates of the simulator from the following git repository
https://jiachengy@bitbucket.org/jiachengy/fruit.git