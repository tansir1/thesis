import numpy as np



class ObjectivesData:

    def __init__(self):
        self.detected = None
        self.destroyed = None
        self.known = None
        self.plotMarker = 'o'
        self.plotColor = None
        self.plotLabel = None



#Data values
comm100 = ObjectivesData()
comm100.detected = np.array([  86,  74, 111, 65, 191,  64, 205,  71, 118, 175])
#Skip world 3
#comm100.destroyed = np.array([172, 159, 160, -1, 248, 175, 307, 130, 280, 221])
comm100.destroyed = np.array([172, 159, 160, 248, 175, 307, 130, 280, 221])
comm100.known = np.array([     85,  68,  82, 95,  16,  58,  95,  20,  63,  29])
comm100.plotColor = 'blue'
comm100.plotLabel = 'Rng 100%'

comm20 = ObjectivesData()
comm20.detected = np.array([122, 66, 176, 61, 144, 117, 238, 166, 261, 156])
comm20.destroyed = np.array([348, 168, 196, 179, 244, 238, 260, 209, 300, 251])
comm20.known = np.array([104, 76, 41, 22, 20, 107, 74, 157, 256, 47])
comm20.plotColor = 'green'
comm20.plotLabel = 'Rng 20%'

comm10 = ObjectivesData()
comm10.detected = np.array([119, 102, 217, 55, 74, 198, 119, 109, 123, 44])
#Skip world 3
#comm10.destroyed = np.array([305, 378, 294, -1, 174, 244, 170, 222, 165, 125])
comm10.destroyed = np.array([305, 378, 294, 174, 244, 170, 222, 165, 125])
comm10.known = np.array([117, 144, 104, 41, 19, 184, 91, 53, 124, 69])
comm10.plotColor = 'red'
comm10.plotLabel = 'Rng 10%'

comm5 = ObjectivesData()
comm5.detected = np.array([238, 112, 164, 55, 76, 171, 394, 172, 133, 100])
#Skip world 4
#comm5.destroyed = np.array([279, 237, 199, 351, -1, 263, 424, 440, 244, 210])
comm5.destroyed = np.array([279, 237, 199, 351, 263, 424, 440, 244, 210])
comm5.known = np.array([242, 123, 85, 65, 66, 136, 161, 142, 156, 115])
comm5.plotColor = 'black'
comm5.plotLabel = 'Rng 5%'
#comm5.plotColor = 'yellow'

comm2 = ObjectivesData()
comm2.detected = np.array([380, 167, 288, 50, 269, 261, 199, 257, 234, 103])
#Skip world 3 and 9
#comm2.destroyed = np.array([554, 498, 596, -1, 337, 299, 395, 468, 323, -1])
comm2.destroyed = np.array([554, 498, 596, 337, 299, 395, 468, 323])
comm2.known = np.array([339, 468, 233, 164, 93, 166, 332, 183, 293, 110])
comm2.plotColor = 'magenta'
comm2.plotLabel = 'Rng 2%'

#comm_ranges = np.array([100,20,10,5,2])
average_detected = np.array([116, 151, 116, 161, 221])
average_destroyed = np.array([206, 239, 231, 294, 434])
average_known = np.array([61, 90, 95, 129, 238])