# SC2002-Assignment (HDB BTO Management System)

# Overview
The HDB BTO Management System is designed to streamline and manage the entire lifecycle of the Build-To-Order (BTO) process in Singapore. The system allows for the management of various aspects of BTO flats, including project creation, applicant registration, flat bookings, officer registration, and handling of enquiries.

# Features
## Applicant
- **View Eligible Projects**: Applicants can view projects that are open to them based on their marital status (Single or Married).
- **Apply for Projects (Submit Application)**: Applicants can apply for projects. Single applicants can apply for 2-Room flats (if they are 35 years and above). Married applicants can apply for 2-Room or 3-Room flats (if they are 21 years and above).
- **Application Status**: Applicants can track their application status (Pending, Successful, Unsuccessful, Booked).
- **Flat Booking**: Applicants whose application status is Successful can proceed with booking a flat with the HDB Officer.
- **Enquiries**: Applicants can submit, view, and manage enquiries related to the projects they apply for.
- **Withdraw Application**: Applicants can request to withdraw their BTO application, whether before or after flat booking.

## HDB Officer
- **Manage BTO Applications**: HDB Officers can assist applicants with the flat booking process once their application is successful.
- **Flat Booking**: Officers handle the flat booking process, ensuring that the appropriate flat is booked for an applicant based on their application details.
- **Respond to Enquiries**: HDB Officers can view and respond to enquiries about the projects they are assigned to.
- **Registration**: Officers can register to handle a project, subject to approval by the HDB Manager.

## HDB Manager
- **Manage Projects**: Managers can create, edit, and delete BTO projects. Projects can be toggled as visible or hidden for applicants.
- **Approve Officer Registration**: Managers can approve or reject HDB Officer registrations for a project.
- **Approve Applications**: Managers approve or reject BTO applications based on available units.
- **Enquiries**: Managers can view and respond to enquiries from applicants.

# Installation
1. Download the project files or clone the repository.
2. Ensure Java is installed on your system.
3. Ensure you are in the root directory of the project
4. Run the Program
```
cd ..\SC2002-Assignment
javac Main.java  # Compile
java Main        # Run
```

# Data Files
The system makes use of several data files (CSV format) to persist the following information:
- **Projects (ProjectList.csv)**: Details of available BTO projects, including unit count, prices, and dates.
- **Applicants (ApplicantList.csv)**: Contains applicant details such as NRIC, age, marital status, and password.
- **Applications (Application.csv)**: Tracks submitted applications, including status and booking details.
- **Officer Registrations (OfficerRegistration.csv)**: Holds officer registration details, including the project they are assigned to and their registration status.
- **Enquiries (Enquiries.csv)**: Logs enquiries made by applicants for each project.


# Usage 
1. Run Main.java to execute program
2. Input credentials to log in (NRIC and password)
3. Different Menus will be presented based on the type of user that is logged in
  - **Applicants**: Apply for projects, track application status, withdraw applications, and manage enquiries.
  - **Officers**: Manage successful applications, handle bookings, respond to enquiries, and register to handle projects.
  - **Managers**: Create and manage projects, approve officer registrations, manage applications, and respond to enquiries.
4. Input the respective number for action wish to be made
5. Use application as see fit and logout to change user or exit program to terminate program

# Contributors
- NICOLE YAP XIU EN - U2420768C
- POH JIA YONG - U2323306C
- SOH HAO MING - 
- WAYNE CHUA ENG KIAT (CAI RONGJIE) - 

# License
For Academic use only
