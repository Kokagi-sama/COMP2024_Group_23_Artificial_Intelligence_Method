
# COMP2024 Group 23 Project Documentation

## Introduction
This project is part of the COMP2024 coursework, which includes two main components: `aim_coursework` and `custom_aim_framework`. The `custom_aim_framework` is a Maven project responsible for creating a custom Java JAR framework, while `aim_coursework` contains the Java application that utilises this framework to process algorithms related to the coursework.

## Prerequisites
- Java JDK 21 or any other suitable version of JDK.
- Apache Maven 3.9.5 or any other suitable version of Maven. (Note: Maven setup is optional since a pre-compiled Custom Framework's Fat JAR will be provided)

## Setup Instructions

### Custom AIM Framework
The `custom_aim_framework` directory contains the Maven project used to generate the `aimframeworkgrp23.jar`. This JAR file is crucial for the coursework application and is located under `custom_aim_framework/target/`.

#### Generating the JAR (Optional)
If needed, the JAR file can be manually recompiled using the provided `generateFramework.bat` Windows script:
1. Navigate to the `custom_aim_framework/` directory.
2. Run `generateFramework.bat`.
3. The newly compiled JAR will replace the existing one in the `target/` directory.

### AIM Coursework
The `aim_coursework` folder houses the Java application that uses the custom framework.

#### Project Structure
- `src/com/aim/coursework/`: Contains `Main.java` and other Java algorithm files.
- `resources/`: Includes `BPP.txt` dataset and a `results/` folder for algorithm outputs.

#### Running the Program in VSCode
1. Ensure `aimframeworkgrp23.jar` is added to the project libraries.
2. Open the `aim_coursework` directory in VSCode.
3. Configure the project's build path to include `aimframeworkgrp23.jar`.
4. Run `Main.java` to execute the algorithms and generate outputs in the `results/` folder.

#### Note
- Steps to configure the project in other IDEs will vary. Ensure `aimframeworkgrp23.jar` is included in the project build path according to specific IDE instructions.

## References
- [Java Documentation](https://docs.oracle.com/en/java/)
- [Apache Maven Documentation](https://maven.apache.org/guides/)
- [Visual Studio Code Documentation](https://code.visualstudio.com/docs)