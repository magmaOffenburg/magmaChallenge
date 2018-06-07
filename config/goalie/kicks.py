import numpy as np
import random

min_x = -12
max_x = 0
min_y = -9
max_y = 9

min_speed_mod = 1.75
max_speed_mod = 3
min_z_speed = 0
max_z_speed = 5

goal_targets = [[-15,-0.9],[-15,0],[-15,0.9]]


with open("kicks.csv","w") as f:
	for x in range(min_x, max_x+1):
		for y in range(min_y, max_y+1):
			for goal in goal_targets:
				x_dist = goal[0]-x
				y_dist = goal[1]-y
				dist = np.sqrt(x_dist*x_dist+y_dist*y_dist)

				for z in range(min_z_speed, max_z_speed+1):
					f.write(str(x)+";"+str(y)+";"+str(x_dist*min_speed_mod)+";"+str(y_dist*min_speed_mod)+";"+str(z)+"\n")
					f.write(str(x)+";"+str(y)+";"+str(x_dist*max_speed_mod)+";"+str(y_dist*max_speed_mod)+";"+str(z)+"\n")

print("Done")