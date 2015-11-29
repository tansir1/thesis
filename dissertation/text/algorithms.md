
#Algorithms #
Create a state diagram of the task states (Dot, graphviz?)

##Task Allocation##
Contract net variant, no sub tasks??? No response back to task manager/originator
Put this in another file?

##Search Task ##
~~~{.numberLines}
Input: host_uav, world

scanned_cells = null
for cell in all world cells in sensor FOV
  if cell.center is contained in sensor FOV
     scanned_cells += cell

for cell in scanned_cells
  target_type = world.targetAt(cell)
  //Target type can be null if there is no target at the specified cell location
  if target_type is not null
    if sensor.probability_detect(target_type) > random[0,1]
      //Check if this is the first time the target has been seen
      if not host_uav.getBelief().isTargetAt(cell)
        host_uav.getBelief().setTargetAt(target_type, cell)
        host_uav.startTaskAllocation(target_type, cell, CONFIRM)

    TODO: Add probability of target mis-classification
~~~

##Confirm Task##
Confirm identity of target type, requires multiple scans, Start track or Attack

##Track Task##
Very similar to confirm, but follow a moving target. Start attack

##Attack Task##
self explanatory, Start BDA

##Battle Damage Assessment (BDA)##
Focused scans to confirm if target actually destroyed.  Enqueue another track/attack as needed

