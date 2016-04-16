module circuitry

open util/boolean
open util/integer
open util/graph[Grid] //the Grid is graphed
open util/ordering[Circuit] as ord //Circuits are ordered

one sig Grid {
	circuit: some Circuit //Grids have at least one circuit
}

/*Each circuit has 0 or more supply AND 0 or more load*/
abstract sig Circuit {
	supply: set Supply, //each circuit has 0 to inf supply
	load: set Load	//each circuit has 0 to inf load
}

sig Load_Circuit extends Circuit {
	//can it be lone (0 or 1)? -> should probably allow as many as required.
	supply_circuit: set Supply_Circuit //every load posseses 0 to inf
}

sig Supply_Circuit extends Circuit {
	load_circuit: set Load_Circuit //each supply circuit contains 0 to inf load_circuits
}

abstract sig Component {
	containing_circuit: one Circuit //each component belongs to one circuit
}

sig Switch extends Component {
	switch: Circuit -> Circuit //switch in component mapped to some other circuit
}



/*===========SUPPLY==========*/
abstract sig Supply extends Component {
	load: set Load,  //each supply contains 0 to inf load
	watts: one Int //supplied wattage
}

/*Georgia Power*/
sig GP extends Supply{} 
/*Solar Power*/
sig SP extends Supply {}

sig Wind extends Supply {}

sig Geo extends Supply {}

sig Hydro extends Supply {}


/*===========LOAD=============*/
sig Load extends Component {
	supply: one Supply, //singuleton link to supplier
	watts: one Int //demanded wattage
}



/*===========FUNCTIONS==============*/

/*Returns the total supplied wattage of the circuit*/
fun s_watts [c: Circuit]: one Int{
	sum s: c.supply | s.watts
}

/*Returns the total load wattage of the circuit*/
fun l_watts [c: Circuit]: one Int{
	sum l: c.load | l.watts
}

/*Returns the total load wattage of the grid*/
fun grid_l_watts [g: Grid]: one Int {
	sum l: g.circuit.load | l.watts
}

/*Returns the total supplied wattage for the grid*/
fun grid_s_watts [g:Grid] : one Int {
	sum s: g.circuit.supply | s.watts
}

/*Returns the total wattage of a set of Loads*/
fun load_watts [l: Load]: one Int {
	sum l.watts
}

/*Returns the total wattage of a set of Supplies*/
fun supply_watts [s: Supply]: one Int {
	sum s.watts
}



/*==========FACTS=============*/

fact {

	/*All watts for load and supply are between 0 and 10*/
	all l: Load | (integer/gte[l.watts, 0]) and (integer/lte[l.watts,10])
	all s: Supply | (integer/gte[s.watts, 0]) and (integer/lte[s.watts, 10])

	/*All Circuits belong to the Grid*/
	all g: Grid | g.circuit = Circuit

	/*No grid can have a deficit wattage*/
	no g: Grid | (g.grid_l_watts > g.grid_s_watts)

	/*all components must belong to a circuit*/
	all c: Component | #c.containing_circuit > 0
	
	/*No Self-Circuits (no two circuits in the grid are equal)*/
	no g: Grid | g.circuit = g.circuit.nexts
	
	/*For all supplies in a circuit, the total wattage of loads attached to that supply cannot exceed its supplied wattage*/	
	all s: Circuit.supply | (s.load.load_watts =< s.watts)

	/*For all load and supply in circuit, if the supplied wattage of one supply is lower than or equal to the wattage of another load, that load is in the
		set of load of that supply*/
	all l: Circuit.load, s: Circuit.supply | ((s.supply_watts - s.load.load_watts) >= l.watts) <=> (l in s.load)

	/*For all load_circuits and supply_circuits in the grid, if the supplied wattage of one supply circuit is a surplus and greater than required of
		that load circuit, that load is supported by the surplus supply circuit*/
	all l: Load_Circuit, s: Supply_Circuit | ((s.s_watts - s.l_watts) >= (l.s_watts - l.l_watts)) <=> (l in s.load_circuit)
}

/*===ASSERTION CHECK = TRUE====*/
assert non_negative_wattage {
	no g: Grid | g.grid_s_watts < g.grid_l_watts
	no s: Supply_Circuit | s.s_watts < s.l_watts
	no s: Supply | s.watts < s.load.load_watts
}

check non_negative_wattage expect 0 //Expect "no" counterexample

pred blank {

	some Circuit
}
 
/*==Below is a predicate version of "changing the constants". Implemented is an abstracted of what would happen during a down pour during which
		there was not much wind and there is at LEAST one Solar Power supply in the grid, assuring that the model will have to accomodate the 
		 decreased power supply. Additionally, there must be at least one Load (to produce interesting results.)*/

pred SUNLESS_Test {

	all w: SP.watts | w < 2 //All Solar Power supplies produce less than 2 watts
	all w: Wind.watts | w < 3 //All Wind Power supplies produce less than 3 watts
	some s: SP | s in Grid.circuit.supply //There is at least one Solar Power supply in the Grid
	#Grid.circuit.load > 0 //There are more than 0 Loads in the Grid
}

//run SUNLESS_Test for 50 but 3 SP, 4 SP, 5 Load, 8 int

run blank for 20 but 8 Int, 3 seq
