# CS446-ECE452-project

This project consists of multiple git repositories:
- Mobile application: https://github.com/CS446-ECE452-JourneyTogether/CS446-ECE452-project (This repository)
- Database: https://github.com/CS446-ECE452-JourneyTogether/CS446-ECE452-database

## Introduction

This is the readme file for the [JourneyTogether] project. This document provides step-by-step instructions on how to clone the project, connect to Firebase in Android Studio, and create a pull request (PR) for contributing to the project.

## Cloning the Project

To clone the project repository, follow these steps:

1. Open a terminal or command prompt on your local machine.
2. Navigate to the directory where you want to clone the project.
3. Run the following command:

   ```shell
   git clone https://github.com/CS446-ECE452-JourneyTogether/CS446-ECE452-project.git
   ```

4. Once the cloning process is complete, you will have a local copy of the project on your machine.

Or you can directly clone our project in android studio

1. Choose project from version control under "New".
2. Pasting the followin the "URL":

    ```
    https://github.com/CS446-ECE452-JourneyTogether/CS446-ECE452-project.git
    ```


## Connecting to Firebase in Android Studio

To connect your Android Studio project to Firebase, follow these steps:

1. Open Android Studio on your local machine.
2. If the project is already open, navigate to the project you cloned in the previous step. Otherwise, open the project by selecting "Open an existing Android Studio project" and selecting the project directory.
3. Once the project is open, click on the "Tools" menu at the top and select "Firebase" from the dropdown.
4. The Firebase Assistant panel will open on the right side of the screen. If it doesn't appear, click on the Firebase icon in the toolbar.
5. In the Firebase Assistant panel, select the desired Firebase service you want to connect to your project, such as "Authentication" or "Firestore".
6. Follow the on-screen instructions to set up and connect your project to the selected Firebase service.


## Creating a Pull Request (PR)

To contribute to the project by creating a pull request, follow these steps:

1. Make sure you have cloned the project and have it set up locally (as described in the "Cloning the Project" section).
2. Create a new branch for your changes. It's recommended to use a descriptive name that reflects the purpose of your changes. For example:

   ```shell
   git checkout -b my-feature
   ```

3. Make the necessary changes to the project code or files.
4. Once your changes are complete, commit them to your local branch. Use clear and concise commit messages that describe the changes you made. For example:

   ```shell
   git add .
   git commit -m "Add new feature: XYZ"
   ```

5. Push your branch to the remote repository:

   ```shell
   git push origin my-feature
   ```

6. Visit the project's repository on GitHub ([Project Name Repository](https://github.com/CS446-ECE452-JourneyTogether/CS446-ECE452-project)).
7. Click on the "Pull requests" tab.
8. Click on the "New pull request" button.
9. Select the base branch (usually "main" or "master") and the branch with your changes.
10. Add a title and description to your pull request, explaining the purpose of your changes. Be clear and detailed in your description to help the reviewers understand your modifications.
11. Review the changes and, if everything looks good, click on the "Create pull request" button.
12. Your pull request will be submitted and reviewed by the project maintainers. They will provide feedback or merge your changes into the main project if they are deemed suitable.
