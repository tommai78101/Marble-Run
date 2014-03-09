# Marble Run - Accelerometer-based Android Game
---

![](http://i.imgur.com/ITDYv.png "Demo picture of Marble Run")

##### Description:

This is an Android game, created for my Computer Science project. It uses the accelerometer sensor inside Android smartphones and devices to interact and play. The accelerometer controls a red ball. Tilting and turning your device will make the ball to move around. 

##### How to play:

To play, hold your device parallel to a table or a floor. Slowly tilt the device towards the direction where you wanted to go to, until you see the ball starts moving. Collect coins on your way to earn points for your score. Try to get the ball to the hole to advance to the next stage.

---

| Known Issues | Description
| --- | --- |
| **Testing devices are limited.** | I only have 1 Android device available with me. (HTC Evo 3D) |
| **Unable to use emulator for debugging/testing purposes.** | Emulators are not useful for debugging game problems that relies on constant feedback, such as sensor data and GPS positions. |
| **Unable to disable Home key, even after setting up multiple flags.** | This is an Android design, it's nothing that I can do. Therefore, once you pressed Home key, and return to the game, it will crash. |
| **Unable to handle Power key properly.** | It will either return to the main menu, or crash the app and cause unexpected orientations. |
| **Compass is off-centered.** | I haven't think up a good way to combat this for multiple devices. I got reports that the compass can be off-centered on both sides of the screen. |
| **Main activity seems to launch twice upon startup.** | It can happen to some devices, but not every time. |

---

##### Support Forum

You can discuss Marble Run here at [The Helper Forums](http://www.thehelper.net/forums), where this project originates from.

---

This work is Apache Licensed.
