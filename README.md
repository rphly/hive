# Hive
![hive](https://i.imgur.com/tbqLryV.png)

## Team 2B

Raphael Yee &nbsp;&nbsp; @rphly 1005292
<br/>
Joyce Lim&nbsp;&nbsp; @joyceeqq 1005307
<br/>
Christopher Lye Sze Kian &nbsp;&nbsp; @chris-lye 1004993
<br/>
Shaun Neo Kay Hean &nbsp;&nbsp; @shaunneo 1005201
<br/>
Atisha Teriyapirom &nbsp;&nbsp; @tishteri 1005244
<br/>
Jodi Wu Wenjiang &nbsp;&nbsp; @jodiwu 1005224
<br/>
Fu Meihui &nbsp;&nbsp; @meihuikkk 1004864
<br/>


### NOTE ⚠️
<b>This project requires a firebase google-services.json file to run. It should be pushed to master. If not, please contact anyone in the group for the credentials file.</b>

## Introduction of Hive
Hive is an Android smartphone app that provides better desk management for <b>smarter, more flexible offices</b>. Following the impact of the COVID-19 pandemic, many companies have adapted accordingly and have the majority of their workers working from home. However, current office spaces are designed around allocated seats and not adapted to the post-covid work culture. Current workspaces are also not conducive for highly focused work and unpersonalised as it cannot be adjusted according to user preferences. With Hive, seats are allocated more efficiently where users can log in to a seat only when needed. The interactive map and search function available in Hive further promotes collaboration between coworkers as one can easily find where their coworkers are allocated. With simple gestures like flipping over the phone, one can indicate to your coworkers via Hive that you are currently in focus mode, which promotes higher productivity. A desk control panel, which allows control of light brightness and temperature through simple clicks of buttons and shaking gestures, makes office space more personalized to user’s preferences, allowing workers to be comfortable in the office space. 

## Folder Structure
Our app is split into different folders, each comprising of files that handle a specific part of our app. The folders are split as such:
```
Folders:

app/src/main/java/com/example/hive
|
└─── activities             
│ 
└─── adapters
│ 
└─── fragments
│ 
└─── models
│ 
└─── services
│
└─── utils
```

## System Design and Implementation
1. <b>Android Java App</b>: The android app is coded in Java and built in android studio.

2. <b>Firebase:</b> Firebase authentication implements the registration and login system, and stores the users and desks data in the Realtime Database, with the profile pictures stored in Cloud Storage.

3. <b>LIFX</b>: A HTTP API used for controlling our Internet of Things connected smart light.

4. <b>QR code</b>: They are used to uniquely identify desks in the office. When a user logs into a desk via the QR code, the unique desk will then be logged in by the user as shown in the interactive map.

5. <b>Sensey Android Library</b>: Used to detect user gestures and invoke functionality such as turning the lights on and off and setting user status to “do not disturb”.

_Our backendless architecture_
![SystemArchi](https://github.com/rphly/hive/blob/master/Images/SystemArchi.jpg)

## Highlights of the concepts used
1. <b>Implicit & Explicit intent</b>: Implicit intent is used to redirect the user to an external email application when they tap on another user’s email. Explicit intent is used to launch various activities.

2. <b>Adapter and RecyclerView</b>: It is used to display a list of users in our search function to find their teammates.

3. <b>OnTouchListener</b>: OnTouchListener is used to allow users to scroll the office map and for users to visually see where other teammates are located.

4. <b>OnClickListener</b>: OnClickListeners are used throughout the app, to allow for the app to respond to user interactions, such as submitting user data during registration and login, or altering profile data.

5. <b>Singleton design pattern</b>: This design pattern is used to handle external LIFX API calls to ensure API keys are not stale; no dangling LIFX instance with stale API key.

6. <b>OnBindViewHolder</b>: It is used to populate user information in our search function to find their teammates.

7. <b>OnTextChanged</b>: It is used to filter users from our list when users type in a username in the search bar to find their teammates.

8. <b>Interface</b>: It was used to ensure asynchronous getting and setting of data via the Response interface, which ensures that onSuccess and onFailure callbacks are implemented.

9. <b>Activity Lifecycle - onCreate, onDestroy etc</b>: Each activity after the log in has a super call to the AuthenticatedActivity class, to ensure that a user is logged in before being able to access any of the activities. In the GestureRemote activity, onDestroy is used to destroy the current instance of the Sensey object, this is done to stop any gesture detection after the user leaves the GestureRemote activity. Any gestures to the phone outside of the GestureRemote activity will not be detected and reflect any calls to the LIFX API.

10. <b>Using java to create and edit widgets (buttons)</b>: A custom makeButton function was used to create the User buttons iteratively, and set display of the user profile, as well as location of user based on their occupied desk accordingly.

11. <b>Toasts</b>: Used in the updating of the user’s profile page, providing feedback when the change is successful or unsuccessful via a toast pop-up.

12. <b>Ploading and parsing of images</b>: getBitmap() was used to retrieve bitmap data from the user gallery for uploading to the Cloud Storage. This allows retrieval of bitmap data anytime the user profile picture is needed to be displayed, such as in the FragmentMap or UserDetailsBottomSheet fragments.


13. <b>Encapsulation/Separation of Concerns + Static factory methods</b>: We use data models such as UserModel, DeskModel, LightModel to encapsulate logic related to a particular object in its own class. This allows us to re-use helper functions, and use static factory methods to create objects from API calls.

14. <b>Inheritance</b>: LIFXService, UserService inherits from a BaseService class that implements commonly used calls to Firebase. 

15. <b>Threading</b>: We use threading to poll our database for updates. As we are unable to run asynchronous / blocking calls on the main UI thread, we have to create a separate thread to poll for updates. This allows us to refresh stale data inside our app.

_How we organize our code to improve modularity, encapsulation and productivity_
![organization]()