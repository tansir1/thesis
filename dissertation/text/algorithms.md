
#Algorithms #
Create a state diagram of the task states (Dot, graphviz?)

##Task Allocation##
Contract net variant, no sub tasks??? No response back to task manager/originator
Put this in another file?

##Search Task ##
This may or may not be a top-level task.  By making it a top-level task it gets
trumped by more active tasks so searching gets deprioritized.  Look into
converting search into a sub-function of the other tasks so that if the currently
active task cannot be performed immediately do search instead.  If nothing else
at least scan the visible range around the UAV; the UAV flight path does not need
to be altered in that case.

~~~{.numberLines}
Input: host_uav, world, sensor

//Cells are in FOV if cell.center is within FOV
cells_in_fov = world.getCellsInFOV(sensor)

for each cell in cells_in_fov
  target = world.targetAt(cell)
  //Target can be null if there is no target at the specified cell location
  if target is not null
    //probability_detect() does account for the angle difference 
    //between the sensor and target heading
    if sensor.probability_detect(target) > random[0,1]
      //Sensor has detected the target
      
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
If the target is unmobile align the UAV with the preferred attack angle
 and deploy a weapon.  If the target is mobile attempt to approach from
 the preferred attack angle but do not require it for deployment.
Align the UAV with the 
self explanatory, Start BDA

~~~{.numberLines}
Input: uav, target, weapon, world

dist_to_target = uav.location.distanceTo(target.location)
if dist_to_target > weapon.range
  Keep flying towards target
else
  //The UAV just reached weapon range of the target
  preferred_angle = target.heading - weapon.preferredAngle(target.type)
  preferred_angle_delta = uav.heading - preferred_angle
  
  target_cell = world.toCell(target.location))
  
  if(math.abs(preferred_angle_delta)) < weapon.launch_angle
    //Weapon and target are ideally aligned, deploy the weapon
  
    //deploy() accounts for probability of destruction given relative 
    //angles between the weapon and target
    weapon.deploy(target)
    uav.task.status = Complete
    uav.task.statusChangeTime = current_time
    uav.startTaskAllocation(target.type, target_cell, BDA)
  else if not target.isMobile
    //Target is a building.  Keep flying until the preferred angle is reached.
  else
    //The target is mobile and not near the preferred launch angle.
  
    //Computes the time to reach a launch acceptability region given the 
    //UAV's flight performance, weapon characteristics, and the target's velocity.
    time_to_LAR = uav.timeToIntercept(target, weapon)
    if time_to_LAR < 0
      //Not possible to intercept, deploy weapon anyway
      weapon.deploy(target)
      uav.task.status = Complete
      uav.task.statusChangeTime = current_time
      uav.startTaskAllocation(target.type, target_cell, BDA)
    else
      //Keep flying until the preferred angle is reached 
~~~


##Battle Damage Assessment (BDA)##
Focused scans to confirm if target actually destroyed.  Enqueue another track/attack as needed.

~~~{.numberLines}
Input: uav, target, sensor, world

dist_to_target = uav.location.distanceTo(target.location)
if dist_to_target > sensor.range
  Keep flying towards target
else if uav.task.status == EnRoute
  //The UAV just reached sensor range of the target
  uav.task.status = InProgress
  uav.task.statusChangeTime = current_time
  
  //Request the host UAV to fly a loiter pattern around the target 
  uav.loiter(target.location, sensor.range)
else if uav.task.status == InProgress
  //The UAV is loitering around the target
  sensor.pointAt(target.location)
  
  //For simulation purposes assume BDA always takes 10 seconds
  if (current_time - uav.task.statusChangeTime) > 10 seconds
    //No matter the result of the BDA scan, the task is completed
    uav.task.status = Complete
    uav.task.statusChangeTime = current_time
  
    if target.isActive()
      //Target not destroyed, try again
      target_cell = world.toCell(target.location))
      
      //Current UAV is likely to win the task allocation process, but not guaranteed
      if target.isMobile()
        host_uav.startTaskAllocation(target.type, target_cell, TRACK)
      else
        host_uav.startTaskAllocation(target.type, target_cell, ATTACK)
    else
      //Target destroyed, revert to only searching
      uav.task = SEARCH
      
~~~

##UAV Path Planning##
~~~{.numberLines}
Input: uav

/*
*TODO: Adjust waypoint to be far enough away from world bounds to 
*allow the UAV to turn around before leaving the theatre.
*/

/*
*TODO: Compute spline from uav location to target with at least one intermediate
*point that is some distance away at the preferred target approach angle.
*/

Example: Approach target from due south, requires UAV to curve instead of a 
straight line approach

               Target
                 |
                 |  <--intermediate point2
                /             
               /
UAV-----------/  <--intermediate point1
*/

//Naive implementation.  Fly straight towards the waypoint.
bearingTo = uav.location.bearingTo(uav.toWaypoint)
if uav.heading is not equal bearingTo
  uav.heading += min(uav.heading - bearingTo, uav.maxTurnRate)

~~~