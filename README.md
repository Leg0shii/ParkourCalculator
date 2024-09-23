# ParkourCalculator

## Overview
ParkourCalculator is an advanced tool designed for simulating and analyzing parkour mechanics in Minecraft. With a focus on versions 1.8.9, 1.12 and 1.20, it offers an in-depth look into the game's physics, blocks, and player interactions.

## Detailed Simulation
- Simulate Minecraft versions 1.8.9, 1.12, 1.20 with precision.
- Analyze player movements and interactions with various blocks.
- Simulate a wide range of player inputs to test different scenarios.
- Move and adjust player paths for thorough examination.

![](readme/vid1.gif)

## Pathfinding
- Utilize advanced AI to generate optimal paths from start to end positions.
- Highly customizable and capable of navigating complex terrain with precision.
- Achieves pathfinding accuracy comparable to world record speedruns.
- Example: https://www.youtube.com/watch?v=-zVK3DKpgr4

## 3D World Editor
- Build or import Minecraft worlds for analysis.
- Manipulate and experiment within a comprehensive 3D environment.
- Export worlds from MPK2.0 into the ParkourCalculator.

![](readme/vid2.gif)

## Export Macros
- export macros for practical application in gameplay.

![](readme/vid3.gif)

## Installation
### Windows
1. Download and install the JDK20 which you can find here: https://www.oracle.com/de/java/technologies/downloads/#jdk20-windows
2. Download and double-click on the ParkourCalculator jar and run it

### MacOS
1. Download and install the JDK20: https://www.oracle.com/de/java/technologies/downloads/#jdk20-windows
2. Download the JavaFX 20.0.1 SDK. Before; figure out your system architecture (x64 or aarch64): https://gluonhq.com/products/javafx/
3. Unzip the JavaFX file and place it in any folder and copy the path to the lib file inside the javafx folder
4. Open up a terminal and navigate to the ParkourCalculator jar file.
5. Run this Command: java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar /path/to/your/app.jar

