# NoteSieve

NoteSieve is a combination of two words - notification and sieve (which stands for filtering). The app enables the user to have a more personalized and accessible experience for managing notifications. It bridges the gap between importance of valuable notification and challenge of handling them as a collective, allowing users access what is important for them.


# About the App - 

  • App intercepts the notifications received via a service and stores them in the local database. 

  • It consists of three tabs - All, Grouped and Starred.

  • The All tab has all the notifications that have been captured by the app

  • Users have the option to star / unstar a notification and the ones starred would appear on the Starred screen

  • Grouped screen on the first interaction would show the user a grid of all the apps for which the notifications have been captured and clicking on any of the apps would open up notifications for that clicked app.

  • Each screen has a search bar to filter the notifications based on the given search query.

  • Links inside the notification content with a certain schema can be detected and would open up in a browser on click.

# Tech Stack and additional tools -

  • MVVM architecture with Jetpack Compose for UI

  • RoomDB for storing the notifications locally 

  • Hilt for Dependency Injection

  • NotificationListenerService to intercept the posted notifications

  • LeakCanary for detecting memory leaks


PS: More functionalities and improvements are in the pipeline to provide a better experience in terms UI, UX and performance.  




## Screenshots

<p float="left">
  <a href="https://github.com/user-attachments/assets/09753574-587c-48e9-9f99-f6095b9215c4" style="margin-right: 10px; margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/09753574-587c-48e9-9f99-f6095b9215c4" width="300"  />
  </a>
  <a href="https://github.com/user-attachments/assets/395de934-c7e2-4438-8fd8-8a4b2982b9fd" style="margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/395de934-c7e2-4438-8fd8-8a4b2982b9fd" width="300"  />
  </a>
</p>

<p float="left">
  <a href="https://github.com/user-attachments/assets/ccc364d2-15b0-4939-86a2-fdbc6b49fc3d" style="margin-right: 10px; margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/ccc364d2-15b0-4939-86a2-fdbc6b49fc3d" width="300"  />
  </a>
  <a href="https://github.com/user-attachments/assets/c75f4b37-5c29-4110-9190-086d43cc04b8" style="margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/c75f4b37-5c29-4110-9190-086d43cc04b8" width="300"  />
  </a>
</p>
<div style="text-align: center;">
  <a href="https://github.com/user-attachments/assets/3328f98f-9cd4-4727-a2f4-6290f1e193d0" style="margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/c5f26145-5fe0-4907-90dd-368ba5c727ab" width="300" />
  </a>
</div>

# Version V2 - 
- Drawer added to accommodate more screens
  
    Home - different ways to view notifications from bottom tabs from previous version
  
    Delete - select apps and time range and delete them
  
    Feedback - let know of feedback / issues you might have faced as a user in a form
- Option to share, copy and delete each individual card by clicking on it

<p float="left">
  <a href="https://github.com/user-attachments/assets/723abb2a-47e6-487c-9dce-613a965ca01f" style="margin-right: 10px; margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/723abb2a-47e6-487c-9dce-613a965ca01f" width="300"  />
  </a>
  <a href="https://github.com/user-attachments/assets/063a4bcc-0fbc-42f1-b417-c2a6b47940c0" style="margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/063a4bcc-0fbc-42f1-b417-c2a6b47940c0" width="300"  />
  </a>
</p>

<p float="left">
  <a href="https://github.com/user-attachments/assets/1194945d-d8d6-49ae-84b7-2e17e9297b7e" style="margin-right: 10px; margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/1194945d-d8d6-49ae-84b7-2e17e9297b7e" width="300"  />
  </a>
  <a href="https://github.com/user-attachments/assets/1359d41c-3020-4428-90fd-97a0574211dd" style="margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/1359d41c-3020-4428-90fd-97a0574211dd" width="300"  />
  </a>
</p>
<div style="text-align: center;">
  <a href="https://github.com/user-attachments/assets/d2d324cf-c375-4e36-b67d-b8f45cf386bd" style="margin-bottom: 20px;">
    <img src="https://github.com/user-attachments/assets/d2d324cf-c375-4e36-b67d-b8f45cf386bd" width="300" />
  </a>
</div>




























