# SC2002-Assignment

## Tracker / Progress Flow

### Updates
- Changed download new csv file instead of xls <br/>
- Uncomment codes that you're working on. E.g. if you're working on officer, uncomment them at login and main files.

### New interfaces
- AuthService (Can be expanded in the future for biometrics/other ways login besides password) [OCP Principle]<br/>

### New classes
- Main, User, Manager <br/>
    - to add Officer, Project, Application etc<br/>
- FileReader, ApplicantFileReader, ManagerFileReader Class (Read data from CSV NOT XLSX)<br/>
    - to add OfficerFileReader (Use ManagerFileReader as reference) etc<br/>
- LoginController, ManagerController (Handles methods' logic) <br/>
    - to add OfficerController etc<br/>
- LoginView, ManagerView (Print UI for Login, and different interface for different roles)<br/>
    - to add ApplicantView, OfficerView (Can use ManagerView as reference) etc <br/>
