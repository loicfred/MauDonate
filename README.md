# MauDonate Donation Mobile PWA
This project is a Donation application with the goal to act as a bridge between people in need and donors.  
It was made for my **Agile Project Management** module at university with the objective to demonstrate our ability to work with agile techniques and achieve a few SDGs.  
I used this project to expand my **Java Springboot** development skills at the same time.

**Applied Skills:**
- Java Springboot (MVC front-end, controller, back-end, OAuth2...)
- Authentication (Login, Signin, SSO, Password Reset, Email Verification...)
- Progressive Web Application
- Paypal Sandbox
- MariaDB Database

## Home
The first page of the PWA involves 4 tab, the main tab, donations tab, campaigns tab and about tab.
They can be navigated through swiping left and right.

<img width="750" alt="aa" src="https://github.com/user-attachments/assets/0253f81c-4c41-450b-b7be-f9ba36d65568" />

# Fundraise
The application supports Paypal (Sandbox) as primary payment method for fundraising.  
Donors can donate money to the organisation and will also receive a notification and email for it.

<img width="750" alt="image" src="https://github.com/user-attachments/assets/33269a16-99f9-49b8-bef7-54b7752bee29" />

# Billing
Users can also view their billing history and donation history to keep track of the items they donated and to who they donated.

<img width="750" alt="image" src="https://github.com/user-attachments/assets/676bada1-3193-4c75-9efa-9ef1a3708bd2" />

# Settings
Allows users to edit their information.

<img width="750" alt="image" src="https://github.com/user-attachments/assets/0a84c539-5000-4281-bcdd-4447c20052b8" />

# Admin Panel
The admin panel consist of a 4 swippable tabs similar to home each, allowing the admins to approval requests and donations.  
They can also manage their warehouse storage capacity and edit any database item they want.  
The database accessor generates edit forms of each page with the use of **Reflection**.  
The admin is also able to send emails to users while approving or denying.

<img width="750" alt="image" src="https://github.com/user-attachments/assets/326a0637-ec71-4c4a-8f4d-e7a4a9c1c008" />

# Login & Sign up
Allows users to log in or sign up on the platform. There is also single sign-on available for Google.  
The application also supports password resets and email verification with expiry time.

<img width="750" alt="image" src="https://github.com/user-attachments/assets/8397b4b3-f526-40ea-b274-71a119507550" />
