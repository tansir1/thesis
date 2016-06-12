
#Algorithms #
![UAV task state machine](./diagrams/uav_task_state.png)

##Main Simulation Loop##
~~~{.numberLines}
simTime += SIM_STEP_RATE

for target in world.getAllTargets()
  //Mobile targets will move during the update
  target.update(SIM_STEP_RATE)
for uav in world.getAllUAVs()
  uav.update()
~~~

##Target Update Loop##
Targets have an 'update(delta_time, world)' method that gets invoked by the main 
simulation loop.  Only mobile targets do any actions during the update.

~~~{.numberLines}
update(delta_time, world)
  if self.isMobile
    if self.location == self.route.endWaypoint()
      //Target is sitting inside a haven
      
      if self.havenTimer == 0
        //Target just arrived at the haven.  Sit here for some time.
        self.havenTimer = current_time
        self.havenTimeLimit = random[5,30]//Random 5-30 seconds 
      else
        //Target has been sitting at the haven for a while.  See if it's time to leave.
        self.havenTimer += delta_time
        if self.havenTimer >= self.haveTimeLimit
          self.havenTimer = 0
          do
            haven = world.getRandomHaven()
          while haven.location == self.location
          self.route = world.findRoute(self.location, haven.location)
    else if self.location == road intersection
      //Road intersections serve as routing waypoints
      nextWypt = self.route.getNextWaypoint()
      self.heading = self.location.bearingTo(nextWypt)
    else
      //Target is moving along a road
      position_delta_east = cos(self.heading) * self.speed
      position_delta_north = sin(self.heading) * self.speed)
      self.location.translate(position_delta_north, position_delta_east)
~~~

##UAV Update Loop##
~~~{.numberLines}
update(delta_time, world)

  //If the UAV is at a waypoint, turn towards the next one
  if self.location == self.route.to_waypoint()
    //Turning rate limits are accounted for in route generation, no need to
    //check them here.
    self.heading = self.location.bearingTo(self.route.getNextWaypoint())
    self.route.iterateWaypoint()
  
  position_delta_east = cos(self.heading) * self.speed
  position_delta_north = sin(self.heading) * self.speed)
  self.location.translate(position_delta_north, position_delta_east)
  
  //Check the receiving queue for messages, propagate them out to all other UAVs in range
  //Uses Probabilistic Flooding algorithm to determine whether a message is sent or not
  self.processCommunicationsRelay()
  
  //Process all belief state updates from neighboring UAVs (if any)
  for belief_update in self.recv_queue.getAllBeliefUpdates()
    self.mergeBeliefUpdate(belief)

  //Respond to all contract_announcements  
  for contract_announce in self.recv_queue.getAllAnnouncements()
    bid = self.computeBid(contract_announce) 
    if bid >>> self.task.bid or self.task == SEARCH
      self.sendBid(bid, contract_announce)
      self.pendingBids += bid
      //TODO The Search task can get starved because of this logic
    else
      //Inform the sender this uav is not interested
      self.sendBid(NULL_BID, contract_announce)
   
  for contract_bid in self.recv_queue.getAllBids()
    //Store all bids until bidding closes
    self.processBid(contract_bid)
  //If the bidding times out notify the winner and losers of contracts this
  //uav is managing.
  self.closeOutCompetingContracts(delta_time)
   
  //Clear pending bids for contracts that have been lost
  for loss in self.recv_queue.getAllLosses()
    self.pendingBids -= loss
   
  //It's an edge case but it's possible that a UAV wins multiple contracts simultaneously
  contract_wins = self.recv_queue.getAllWins()
  
  preferred_contract = contract with max benefit from contract_wins
  remaining_contracts = contract_wins - preferred_contract
  if(self.task not SEARCH)
    //The uav is switching it's current task, so the old one must be re-assigned
    remaining_contracts += self.task
    
  //Re-compete contracts as needed
  for contract in remaining_contracts
    self.startTaskAllocation(contract)

  //Task updates can trigger new flight paths
  self.task.update(delta_time)
 
  //Send the current belief state out to neighboring UAVs if it changed
  if self.belief.isChanged()
    //Reset the internal change flag
    self.belief.clearChanged()
    self.transmitBelief()
~~~

##Task Allocation##
TODO: Variant of Contract Net.  Sub tasks are not contracted out (except maybe 
cooperative tracking and attack???).  Once a contract (task) is complete results
are not sent back to the Contract originator...which makes this sound like an
auction again instead of a Contract Net.


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
      
        //Computes a set of {target_type, probability} pairs given sensor
        //characteristics, world truth data, and relative angles between the
        //sensor and target.
        types_and_probs = sensor.estimateTargetType(target.location, world)
        uav.getBelief().updateTypeEstimate(target.location, types_and_probs)
        
        host_uav.startTaskAllocation(target_type, cell, CONFIRM)
~~~

##Confirm Task##
Confirm identity of target type and the target's heading, requires multiple scans over a period of time.

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

  //Computes a set of {target_type, probability} pairs given sensor
  //characteristics, world truth data, and relative angles between the
  //sensor and target.
  types_and_probs = sensor.estimateTargetType(target.location, world)
  //Averages the new set of estimates with previously estimated values
  uav.getBelief().updateTypeEstimate(target.location, types_and_probs)
  
  
  //Estimates the target's heading based on relative angles.  Random
  //noise is injected in the estimation to more accurately simulate
  //real world sensors.
  estimated_hdg = sensor.estimateHeading(target)
  
  //Averages the given heading with previously estimated headings
  uav.getBelief().updateHeading(target, estimated_hdg)
  
  //For simulation purposes assume Confirmation always takes 5 seconds
  if (current_time - uav.task.statusChangeTime) > 5 seconds
    //No matter the result of the Confirmation, the task is completed
    uav.task.status = Complete
    uav.task.statusChangeTime = current_time      
    if uav.getBelief().getBestTypeEstimate(target.location).isMobile()
      uav.startTaskAllocation(target.type, target_cell, TRACK)
    else
      uav.startTaskAllocation(target.type, target_cell, ATTACK)
    
  //TODO: Would it make the simulation better to keep 'confirming' until
  //the change in estimation between scans falls below a threshold?  Would
  //require some kind of gradient descent mechanism in the truth versus
  //estimation detection model.
~~~

##Track Task##
This seems redundant considering the attack task will require a UAV to move into
weapons range.  If this is a separate task then a new bidding process will
occur when the task is complete.  This means when tracking is 'done' another UAV
might win the attack task and have to fly to the target which is still moving.  This
is redundant.  

TODO: Should tracking be an 'ending' task requiring UAVs to
keep an active eye on the target throughout the mission instead of destroying it.

TODO: Could/Should this task be a cooperative task?  One UAV can track the target while another
UAV is flying into position in order to attack.

##Attack Task##
If the target is immobile align the UAV with the preferred attack angle
 and deploy a weapon.  If the target is mobile attempt to approach from
 the preferred attack angle but do not require it for deployment.

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