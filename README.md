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
1. Android Java App: The android app is coded in Java and built in android studio.
2. Firebase: Firebase authentication implements the registration and login system, and stores the users and desks data in the Realtime Database, with the profile pictures stored in Cloud Storage.
3. LIFX: A HTTP API used for controlling our Internet of Things connected smart light.
4. QR code: They are used to uniquely identify desks in the office. When a user logs into a desk via the QR code, the unique desk will then be logged in by the user as shown in the interactive map.
5. Sensey Android Library: Used to detect user gestures and invoke functionality such as turning the lights on and off and setting user status to “do not disturb”.
![SystemArchi](https://github.com/rphly/hive/blob/master/Images/SystemArchi.jpg)






